package lv.latcraft.devternity.cards

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.s3.model.PutObjectRequest
import groovy.util.logging.Commons
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil
import lv.latcraft.utils.WebHook

import static lv.latcraft.utils.FileMethods.temporaryFile
import static lv.latcraft.utils.S3Methods.anyoneWithTheLink
import static lv.latcraft.utils.S3Methods.s3
import static lv.latcraft.utils.SanitizationMethods.sanitizeName
import static lv.latcraft.utils.SvgMethods.renderPNG
import static lv.latcraft.utils.XmlMethods.setAttributeValue
import static lv.latcraft.utils.XmlMethods.setElementValue

@Commons
class CardGenerator {

  public static final String BUCKET_NAME = 'devternity-images'

  static Map<String, String> generate(Map<String, String> data, Context context) {
    log.info "STEP 1: Received data: ${data}"
    CardInfo card = new CardInfo(data)
    File svgFile = temporaryFile('card', '.svg')
    File imageFile = temporaryFile('speaker-image', '.png')
    log.info "STEP 2: Created temporary files"
    imageFile.bytes = new URL(card.speakerImage).bytes
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
    "card-${card.speakerName.toLowerCase().replaceAll(' ', '-')}.${extension}".toString()
  }

  static String getSvgTemplate(String cardType) {
    String templateName = "${cardType}.svg"
    getClass().getResource("/${templateName}")?.text ?: new File(templateName).text
  }

  static String prepareSVG(String svgText, CardInfo card, File image) {
    GPathResult svg = new XmlSlurper().parseText(svgText)
    setElementValue(svg, 'speaker-name', sanitizeName(card.speakerName))
    setElementValue(svg, 'speech-title-line-1', sanitizeName(card.speechTitleLine1))
    setElementValue(svg, 'speech-title-line-2', sanitizeName(card.speechTitleLine2))
    setAttributeValue(svg, 'speaker-image', 'xlink:href', "data:image/png;base64,${image.bytes.encodeBase64().toString().toList().collate(76)*.join('').join(' ')}".toString())
    XmlUtil.serialize(svg)
  }

}
