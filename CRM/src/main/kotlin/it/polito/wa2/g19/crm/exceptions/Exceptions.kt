package it.polito.wa2.g19.crm.exceptions

class CustomerNotFoundException(message: String) : RuntimeException(message)
class ProfessionalNotFoundException(message: String) : RuntimeException(message)
class JobOfferNotFoundException(message: String) : RuntimeException(message)
class ProfessionalNotAvailableException(message: String) : RuntimeException(message)
class InvalidStatusException(message: String) : RuntimeException(message)
class MessageNotFoundException(message: String) : RuntimeException(message)
class ContactNotFoundException(message: String) : RuntimeException(message)
class DuplicatedDataException(message: String) : RuntimeException(message)
class InvalidDataException(message: String) : RuntimeException(message)
class EmailNotFoundException(message: String) : RuntimeException(message)
class TelephoneNotFoundException(message: String) : RuntimeException(message)
class AddressNotFoundException(message: String) : RuntimeException(message)