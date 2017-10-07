(ns name-bazaar.shared.smart-contracts) 

(def smart-contracts 
{:auction-offering-factory
 {:name "AuctionOfferingFactory",
  :address "0x080cf0aadd03a97ca4f9ca6d0f1accd5a736d2a9"},
 :buy-now-offering-library
 {:name "BuyNowOfferingLibrary",
  :address "0xc0951b5195fcbee0a7b1df42f0f0aecb0867fb35"},
 :buy-now-offering-factory
 {:name "BuyNowOfferingFactory",
  :address "0x70c352c981b239c37ee8a34dd7ac8bf006e9a069"},
 :registrar
 {:name "Registrar",
  :address "0x542d337975c2f9b436621024df371244bd53a0af"},
 :buy-now-offering
 {:name "BuyNowOffering",
  :address "0x0000000000000000000000000000000000000000"},
 :auction-offering-library
 {:name "AuctionOfferingLibrary",
  :address "0x5e5321c1ec627c158196143ce8b87a48f4946897"},
 :deed
 {:name "Deed", :address "0x0000000000000000000000000000000000000000"},
 :ens
 {:name "ENS", :address "0x03a4ef49b685ebb9e6ec1895baad76ee14765afa"},
 :mock-registrar
 {:name "MockRegistrar",
  :address "0xe126367fbf0a4fb17a1f734fbd39d6fa886aa5be"},
 :offering-registry
 {:name "OfferingRegistry",
  :address "0xa154859ae0f95320e76e6edfda4d0e5c6d39dfb9"},
 :district0x-emails
 {:name "District0xEmails",
  :address "0x9636ad87d1f18587bd744fbe7513cc371ddbfa55"},
 :offering-requests
 {:name "OfferingRequests",
  :address "0xafe28dbdb078a836b4233fb21922e519b61c9a09"},
 :offering-library
 {:name "OfferingLibrary",
  :address "0xb1508ead32d6f259b1fa0f7aed8535d83d2ae1ed"},
 :auction-offering
 {:name "AuctionOffering",
  :address "0x0000000000000000000000000000000000000000"}})