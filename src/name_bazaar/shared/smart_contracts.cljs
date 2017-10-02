(ns name-bazaar.shared.smart-contracts) 

(def smart-contracts 
{:auction-offering-factory
 {:name "AuctionOfferingFactory",
  :address "0xf93e60c5d16fc38812e220400d6d2ede5490254f"},
 :buy-now-offering-library
 {:name "BuyNowOfferingLibrary",
  :address "0x3f944da06de398b2e1f1d53e340b0a5f4b96da3b"},
 :buy-now-offering-factory
 {:name "BuyNowOfferingFactory",
  :address "0x58ae0e2a309ad895e5baa3b03f49e240bb952f65"},
 :registrar
 {:name "Registrar",
  :address "0x542d337975c2f9b436621024df371244bd53a0af"},
 :buy-now-offering
 {:name "BuyNowOffering",
  :address "0x0000000000000000000000000000000000000000"},
 :auction-offering-library
 {:name "AuctionOfferingLibrary",
  :address "0x8abeb7aa71ddd751b7cb067da0311e059072ab16"},
 :deed
 {:name "Deed", :address "0x0000000000000000000000000000000000000000"},
 :ens
 {:name "ENS", :address "0x15fdfcf040eac24751d84e5c5873f57ad686fb11"},
 :mock-registrar
 {:name "MockRegistrar",
  :address "0xebed26c16919f85f453248e47c23e8725d05f2d1"},
 :offering-registry
 {:name "OfferingRegistry",
  :address "0x9e7444994bf6c281e243c661de89dbbc58e47c8f"},
 :district0x-emails
 {:name "District0xEmails",
  :address "0xd93e48f2606c14c03b40e631f8371fab6ba67d02"},
 :offering-requests
 {:name "OfferingRequests",
  :address "0x26d5e70e4bfcf09480f683f40a6f4e2393037239"},
 :offering-library
 {:name "OfferingLibrary",
  :address "0xc38b0e51e5704f2eab14301f98a4e81b5a296d5f"},
 :auction-offering
 {:name "AuctionOffering",
  :address "0x0000000000000000000000000000000000000000"}})