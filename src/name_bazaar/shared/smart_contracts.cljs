(ns name-bazaar.shared.smart-contracts) 

(def smart-contracts 
{:auction-offering-factory
 {:name "AuctionOfferingFactory",
  :address "0xde069c46fca418383684e78ee248949bbd9bbd94"},
 :buy-now-offering-library
 {:name "BuyNowOfferingLibrary",
  :address "0x229314d77dc7e237387bd9b87cecd0e17d9429a2"},
 :buy-now-offering-factory
 {:name "BuyNowOfferingFactory",
  :address "0x8a508e1397e09461f522c733f038a5742f7b7f81"},
 :registrar
 {:name "Registrar",
  :address "0x542d337975c2f9b436621024df371244bd53a0af"},
 :buy-now-offering
 {:name "BuyNowOffering",
  :address "0x0000000000000000000000000000000000000000"},
 :auction-offering-library
 {:name "AuctionOfferingLibrary",
  :address "0xaf5b2536b38d1d825da7f7f6de71a778e8ceb72f"},
 :deed
 {:name "Deed", :address "0x0000000000000000000000000000000000000000"},
 :ens
 {:name "ENS", :address "0x8b86a205f88554683ae0dec4ee9aa67d13182578"},
 :mock-registrar
 {:name "MockRegistrar",
  :address "0x076380d337043a69e7e7e9ee3669e972cc2021ce"},
 :offering-registry
 {:name "OfferingRegistry",
  :address "0x433dd77c06ab2e55f01fe860566218f6bfb37f57"},
 :district0x-emails
 {:name "District0xEmails",
  :address "0xcf311129c011f93e027108933c8e23d2f0d313e4"},
 :offering-requests
 {:name "OfferingRequests",
  :address "0x829b954963f46f404b82351c794cc63f8c037c47"},
 :offering-library
 {:name "OfferingLibrary",
  :address "0x415cc1a7f3ab605e90780ac74df3fa840fc69662"},
 :auction-offering
 {:name "AuctionOffering",
  :address "0x0000000000000000000000000000000000000000"}})