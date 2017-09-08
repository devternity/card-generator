package lv.latcraft.devternity.cards

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
@Canonical
class CardInfo {

  String webhook
  String email
  String name
  String company
  String product
  String ticketId
  String when
  String what

}
