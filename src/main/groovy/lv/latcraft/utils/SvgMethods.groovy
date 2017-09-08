package lv.latcraft.utils

import groovy.util.logging.Commons
import groovy.xml.XmlUtil
import org.apache.avalon.framework.configuration.Configuration
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder
import org.apache.avalon.framework.container.ContainerUtil
import org.apache.batik.transcoder.SVGAbstractTranscoder
import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.fop.svg.PDFTranscoder
import java.awt.Color

import static java.lang.Boolean.FALSE
import static java.nio.charset.StandardCharsets.UTF_8
import static lv.latcraft.utils.FileMethods.temporaryFile
import static lv.latcraft.utils.LambdaMethods.insideLambda
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER
import static org.apache.batik.transcoder.XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING
import static org.apache.batik.transcoder.image.ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE
import static org.apache.batik.transcoder.image.ImageTranscoder.KEY_BACKGROUND_COLOR
import static org.apache.fop.svg.AbstractFOPTranscoder.KEY_AUTO_FONTS
import static org.apache.fop.svg.AbstractFOPTranscoder.KEY_STROKE_TEXT

@Commons
class SvgMethods {

  private static final int DEFAULT_DPI = 300

  static File renderPNG(File svgFile) {
    def t = new PNGTranscoder()
    t.addTranscodingHint(KEY_BACKGROUND_COLOR, Color.WHITE)
    t.addTranscodingHint(KEY_PIXEL_UNIT_TO_MILLIMETER, new Float((float) (25.4 / DEFAULT_DPI)))
    t.addTranscodingHint(KEY_XML_PARSER_VALIDATING, FALSE)
    t.addTranscodingHint(KEY_STROKE_TEXT, FALSE)
    t.addTranscodingHint(KEY_AUTO_FONTS, false)
    String svgURI = svgFile.toURI().toString()
    File pngFile = temporaryFile('temporary', '.png')
    try {
      t.transcode(
        new TranscoderInput(svgURI),
        new TranscoderOutput(
          new FileOutputStream(pngFile)
        )
      )
    } catch (TranscoderException e) {
      // Let's be very verbose about error logging, since it's very hard to debug FOP exceptions.
      log.debug(e)
      log.debug(e?.exception)
      log.debug(e?.exception?.cause)
      throw e
    }
    pngFile

  }

}
