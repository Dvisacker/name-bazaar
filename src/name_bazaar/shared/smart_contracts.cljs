(ns name-bazaar.shared.smart-contracts) 

(def smart-contracts 
{:auction-offering-factory
 {:name "AuctionOfferingFactory",
  :address "0xbc089a373edab9c8fd64d3daa5dab170820778e3"},
 :buy-now-offering-library
 {:name "BuyNowOfferingLibrary",
  :address "0xbc9c77ece0c183bf6c778801312cf2715fbcf8dc"},
 :buy-now-offering-factory
 {:name "BuyNowOfferingFactory",
  :address "0xe36814a5a91857b9ce9f6be2de083f201a08ebd3"},
 :registrar
 {:name "Registrar",
  :address "0x542d337975c2f9b436621024df371244bd53a0af"},
 :buy-now-offering
 {:name "BuyNowOffering",
  :address "0x0000000000000000000000000000000000000000"},
 :auction-offering-library
 {:name "AuctionOfferingLibrary",
  :address "0x6bf4cbd1bb459f88df88161903e4c8f02a40f26d"},
 :deed
 {:name "Deed", :address "0x0000000000000000000000000000000000000000"},
 :ens
 {:name "ENS", :address "0x5fb8e16b8672b410cbf41c77dd0475e533a2a32b"},
 :mock-registrar
 {:name "MockRegistrar",
  :address "0x9f52705f11ddb494c931628b39b31cb818018ab0"},
 :offering-registry
 {:name "OfferingRegistry",
  :address "0xb0ec8bb7e45ee0bf1691d0412c407c9588bcd0a3"},
 :district0x-emails
 {:name "District0xEmails",
  :address "0x3319f2a53ea94b5692a8833639899d47a1107f50"},
 :offering-requests
 {:name "OfferingRequests",
  :address "0x5fc992c24fccb91ed9a3be7939924762b2f00b79"},
 :offering-library
 {:name "OfferingLibrary",
  :address "0x480c0ae37c5feb93752d39e223884818d98f3479"},
 :auction-offering
 {:name "AuctionOffering",
  :address "0x0000000000000000000000000000000000000000"}})