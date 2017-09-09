package lv.latcraft.devternity.cards

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
@Canonical
class CardInfo {

  String cardType
  String speakerNameFontSize
  String speakerName
  String speechTitleFontSize
  String speechTitleLine1
  String speechTitleLine2
  String speakerImage
  String webHook

}
