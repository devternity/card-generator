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
  String speakerNameFontSize
  String speakerNameShiftX
  String speakerNameShiftY

  String speechTitleLine1
  String speechTitleLine2
  String speechTitleFontSize
  String speechTitleLine1ShiftX
  String speechTitleLine1ShiftY
  String speechTitleLine2ShiftX
  String speechTitleLine2ShiftY

  String speakerImage

  String webHook

}
