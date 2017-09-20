package lv.latcraft.utils

import groovy.util.slurpersupport.GPathResult

class XmlMethods {

  static setElementValue(GPathResult svg, String elementId, String value) {
    findElementById(svg, elementId)?.replaceBody(value)
  }

  static setAttributeValue(GPathResult svg, String elementId, String attributeId, String value) {
    findElementById(svg, elementId)?.@"${attributeId}" = value
  }

  static String getAttributeValue(GPathResult svg, String elementId, String attributeId) {
    findElementById(svg, elementId)?.@"${attributeId}".toString()
  }

  static getAttributeValueAsDouble(GPathResult svg, String elementId, String attributeId) {
    Double.parseDouble(getAttributeValue(svg, elementId, attributeId))
  }

  static addToAttributeDoubleValue(GPathResult svg, String elementId, String attributeId, String value) {
    findElementById(svg, elementId)?.@"${attributeId}" = String.format("%.4f", (getAttributeValueAsDouble(svg, elementId, attributeId) + Double.parseDouble(value)))
  }

  static replaceAttributeValue(GPathResult svg, String elementId, String attributeId, String search, String replace) {
    findElementById(svg, elementId)?.@"${attributeId}" = getAttributeValue(svg, elementId, attributeId).replaceAll(search, replace)
  }

  static replaceParentAttributeValue(GPathResult svg, String elementId, String attributeId, String search, String replace) {
    findElementById(svg, elementId)?.parent()?.@"${attributeId}" = findElementById(svg, elementId)?.parent()?.@"${attributeId}".toString().replaceAll(search, replace)
  }

  static Object findElementById(GPathResult svg, String elementId) {
    svg.depthFirst().find { it.@id == elementId }
  }

}
