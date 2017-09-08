package lv.latcraft.devternity.cards

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import groovy.mock.interceptor.StubFor
import org.junit.Test

class CardGeneratorTest {

  @Test
  void testGenerator() {
    println lv.latcraft.devternity.cards.CardGenerator.generate([
        cardType    : 'BUS',
        speakerName : 'Andrey Adamovich',
        speechTitleLine1 : 'An amazingly cool speech',
        speechTitleLine2 : 'that will change everything',
        speakerImage: 'http://extremeautomation.io/img/signature/andreyadamovich.png',
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
