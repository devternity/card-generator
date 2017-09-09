package lv.latcraft.devternity.cards

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import groovy.mock.interceptor.StubFor
import org.junit.Test

class CardGeneratorTest {

  @Test
  void testGenerator() {
    println CardGenerator.generate([
        cardType    : 'BLACK_CHAOS.embed',
        speakerName : 'Andrey Adamovich',
        speakerNameFontSize: '70',
        speechTitleLine1 : 'An amazingly cool speech',
        speechTitleLine2 : 'that will change everything',
        speechTitleFontSize: '60',
        speakerImage: 'http://devternity.com/images/kief.png',
    ], context)
  }

  private static Context getContext() {
    def context = new StubFor(Context)
    def logger = new StubFor(LambdaLogger)
    logger.demand.log(0..10) { String message -> System.out.println message }
    context.demand.getLogger(0..10) { logger.proxyInstance() }
    (Context) context.proxyInstance()
  }

}
