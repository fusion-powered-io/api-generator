package io.fusionpowered.eventcatalog.apigenerator

import io.fusionpowered.eventcatalog.apigenerator.model.import.DomainImportData
import io.fusionpowered.eventcatalog.apigenerator.model.import.ServiceImportData


interface ApiGenerator {

  suspend fun generate(servicesImportData: Set<ServiceImportData>, domainImportData: DomainImportData?)

}