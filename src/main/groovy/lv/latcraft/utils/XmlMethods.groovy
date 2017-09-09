package lv.latcraft.utils

import groovy.util.slurpersupport.GPathResult

class XmlMethods {

  static setElementValue(GPathResult svg, String elementId, String value) {
    findElementById(svg, elementId)?.replaceBody(value)
  }

  static setAttributeValue(GPathResult svg, String elementId, String attributeId, String value) {
    findElementById(svg, elementId)?.@"${attributeId}" = value
  }

  static replaceAttributeValue(GPathResult svg, String elementId, String attributeId, String search, String replace) {
    findElementById(svg, elementId)?.@"${attributeId}" = findElementById(svg, elementId)?.@"${attributeId}".toString().replaceAll(search, replace)
  }

  static replaceParentAttributeValue(GPathResult svg, String elementId, String attributeId, String search, String replace) {
    findElementById(svg, elementId)?.parent()?.@"${attributeId}" = findElementById(svg, elementId)?.parent()?.@"${attributeId}".toString().replaceAll(search, replace)
  }

  static Object findElementById(GPathResult svg, String elementId) {
    svg.depthFirst().find { it.@id == elementId }
  }

}
