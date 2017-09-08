package lv.latcraft.devternity.cards

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
@Canonical
class CardInfo {

  String cardType
  String speakerName
  String speechTitle
  String speakerImage
  String webHook

}
