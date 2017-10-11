(ns name-bazaar.shared.smart-contracts) 

(def smart-contracts 
{:auction-offering-factory
 {:name "AuctionOfferingFactory",
  :address "0x657357135261bb87b5ebe8008c7223a1129c7c78"},
 :buy-now-offering-library
 {:name "BuyNowOfferingLibrary",
  :address "0xad20b3ce7b08b6e3be73d7d5b96b30d331979903"},
 :buy-now-offering-factory
 {:name "BuyNowOfferingFactory",
  :address "0xa8a6d5ee15ca54683b232831a11970978c07afb6"},
 :registrar
 {:name "Registrar",
  :address "0x542d337975c2f9b436621024df371244bd53a0af"},
 :buy-now-offering
 {:name "BuyNowOffering",
  :address "0x0000000000000000000000000000000000000000"},
 :auction-offering-library
 {:name "AuctionOfferingLibrary",
  :address "0x2d0a7d7f9aef0300e8725ff265e388a5e1d45631"},
 :deed
 {:name "Deed", :address "0x0000000000000000000000000000000000000000"},
 :ens
 {:name "ENS", :address "0x48e8b808d63f8446dfcf30f785654dc310916509"},
 :mock-registrar
 {:name "MockRegistrar",
  :address "0x05b86cfd36810182a8ab9c50e056040694f27575"},
 :offering-registry
 {:name "OfferingRegistry",
  :address "0xabab3ed540bdf4003c9f82e7d725f36c5cda7e0f"},
 :district0x-emails
 {:name "District0xEmails",
  :address "0x380c73c75069258e19845cb40870d21058c1f6c0"},
 :offering-requests
 {:name "OfferingRequests",
  :address "0xaccd5d50f5700c929070d868fae7759212a4cbb5"},
 :offering-library
 {:name "OfferingLibrary",
  :address "0xbd63bddd4a149dd6285a4e5334fa9bfbeea3a85e"},
 :auction-offering
 {:name "AuctionOffering",
  :address "0x0000000000000000000000000000000000000000"}})