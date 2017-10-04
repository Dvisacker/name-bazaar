(ns name-bazaar.shared.smart-contracts) 

(def smart-contracts 
{:auction-offering-factory
 {:name "AuctionOfferingFactory",
  :address "0x5603d83ccc5496e0c0e54db42b2a9f66f2100eeb"},
 :buy-now-offering-library
 {:name "BuyNowOfferingLibrary",
  :address "0xaa8dec24551070dca2eb2ac2105131a7a8558dee"},
 :buy-now-offering-factory
 {:name "BuyNowOfferingFactory",
  :address "0x455e33d2ae6883191b99a70410a25c65abd6d9ac"},
 :registrar
 {:name "Registrar",
  :address "0x542d337975c2f9b436621024df371244bd53a0af"},
 :buy-now-offering
 {:name "BuyNowOffering",
  :address "0x0000000000000000000000000000000000000000"},
 :auction-offering-library
 {:name "AuctionOfferingLibrary",
  :address "0x220b99e40ffd38eff360f6eb717eb8675778a4b9"},
 :deed
 {:name "Deed", :address "0x0000000000000000000000000000000000000000"},
 :ens
 {:name "ENS", :address "0x54aaf9b44b35c3bc1fc8a60151900411aeaac6ed"},
 :mock-registrar
 {:name "MockRegistrar",
  :address "0x89693caafaf7d64debe2427bb03c4f697d938302"},
 :offering-registry
 {:name "OfferingRegistry",
  :address "0x85d3b36fc15587582da5bbed373159440e442320"},
 :district0x-emails
 {:name "District0xEmails",
  :address "0xac40d2eb9398e0ff1f1b97b8b9ce734c2812a91d"},
 :offering-requests
 {:name "OfferingRequests",
  :address "0xb520439ce9d4ccf53a7c0553b03d980f72526449"},
 :offering-library
 {:name "OfferingLibrary",
  :address "0xdd9b8cda42848c8f41df0b846087391fe9b6986d"},
 :auction-offering
 {:name "AuctionOffering",
  :address "0x0000000000000000000000000000000000000000"}})