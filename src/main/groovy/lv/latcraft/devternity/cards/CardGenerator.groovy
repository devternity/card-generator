package lv.latcraft.devternity.cards

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.model.PutObjectRequest
import com.mashape.unirest.http.Unirest
import groovy.util.logging.Commons
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import lv.latcraft.utils.WebHook

import static lv.latcraft.utils.FileMethods.temporaryFile
import static lv.latcraft.utils.S3Methods.anyoneWithTheLink
import static lv.latcraft.utils.S3Methods.s3
import static lv.latcraft.utils.SanitizationMethods.sanitizeName
import static lv.latcraft.utils.SvgMethods.renderPNG
import static lv.latcraft.utils.XmlMethods.addToAttributeDoubleValue
import static lv.latcraft.utils.XmlMethods.setAttributeValue
import static lv.latcraft.utils.XmlMethods.setElementValue
import static lv.latcraft.utils.XmlMethods.replaceAttributeValue
import static lv.latcraft.utils.XmlMethods.replaceParentAttributeValue

@Commons
class CardGenerator {

  public static final String BUCKET_NAME = 'devternity-images'

  static Map<String, String> generate(Map<String, String> data, Context context) {
    log.info "STEP 1: Received data: ${data}"
    CardInfo card = new CardInfo(data)
    File svgFile = temporaryFile('card', '.svg')
    File imageFile = temporaryFile('speaker-image', '.png')
    log.info "STEP 2: Created temporary files"
    imageFile.bytes = Unirest.get(card.speakerImage).asBinary().rawBody.bytes
    log.info "STEP 3: Downloaded speaker image"
    svgFile.text = prepareSVG(getSvgTemplate(card.cardType), card, imageFile)
    log.info "STEP 4: Pre-processed SVG template (${svgFile})"
    File pngFile = renderPNG(svgFile)
    log.info "STEP 5: Generated PNG file (${pngFile})"
    s3.putObject(putRequest(card, pngFile, 'png'))
    log.info "STEP 6: Uploaded PNG file"
    svgFile.delete()

    def response = [
        status: 'OK',
        png   : "https://s3-eu-west-1.amazonaws.com/${BUCKET_NAME}/${cardFileName(card, 'png')}".toString()
    ]

    if (card.webHook) {
      def webHook = new WebHook(url: card.webHook)
      def status = webHook.trigger(response + [data: data])
      log.info "STEP 7: Webhook ($webHook.url) responded with ($status)"
    }

    response
  }

  static PutObjectRequest putRequest(CardInfo card, File file, String extension) {
    new PutObjectRequest(
        BUCKET_NAME,
        cardFileName(card, extension),
        file
    ).withAccessControlList(anyoneWithTheLink())
  }

  private static String cardFileName(CardInfo card, String extension) {
    "card-${card.cardType.toLowerCase().replaceAll('[_\\.]', '-')}-${card.speakerName.toLowerCase().replaceAll(' ', '-')}.${extension}".toString()
  }

  static String getSvgTemplate(String cardType) {
    String templateName = "${cardType}.svg"
    getClass().getResource("/${templateName}")?.text ?: new File(templateName).text
  }

  static String prepareSVG(String svgText, CardInfo card, File image) {
    GPathResult svg = new XmlSlurper().parseText(svgText)
    setElementValue(svg, 'speaker-name', sanitizeName(card.speakerName))
    if (card.speakerNameFontSize) {
      updateFontSize(svg, 'speaker-name', card.speakerNameFontSize)
    }
    if (card.speakerNameShiftX || card.speakerNameShiftY) {
      adjustElementPosition(svg, 'speaker-name', card.speakerNameShiftX, card.speakerNameShiftY)
    }
    setElementValue(svg, 'speech-title-line-1', sanitizeName(card.speechTitleLine1))
    setElementValue(svg, 'speech-title-line-2', sanitizeName(card.speechTitleLine2))
    if (card.speechTitleFontSize) {
      updateFontSize(svg, 'speech-title-line-1', card.speechTitleFontSize)
      updateFontSize(svg, 'speech-title-line-2', card.speechTitleFontSize)
    }
    if (card.speechTitleLine1ShiftX || card.speechTitleLine1ShiftY) {
      adjustElementPosition(svg, 'speech-title-line-1', card.speechTitleLine1ShiftX, card.speechTitleLine1ShiftY)
    }
    if (card.speechTitleLine2ShiftX || card.speechTitleLine2ShiftY) {
      adjustElementPosition(svg, 'speech-title-line-2', card.speechTitleLine2ShiftX, card.speechTitleLine2ShiftY)
    }
    setAttributeValue(svg, 'speaker-image', 'xlink:href', "data:image/png;base64,${image.bytes.encodeBase64().toString().toList().collate(76)*.join('').join(' ')}".toString())
    XmlUtil.serialize(svg)
  }

  private static void adjustElementPosition(GPathResult svg, String elementId, String shiftX, String shiftY) {
    addToAttributeDoubleValue(svg, elementId, 'x', shiftX ?: "0")
    addToAttributeDoubleValue(svg, elementId, 'y', shiftY ?: "0")
  }

  private static void updateFontSize(GPathResult svg, String elementId, String fontSize) {
    replaceAttributeValue(svg, elementId, 'style', "font-size:\\d+px", "font-size:${fontSize}px")
    replaceParentAttributeValue(svg, elementId, 'style', "font-size:\\d+px", "font-size:${fontSize}px")
  }

}
