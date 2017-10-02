(ns name-bazaar.ui.pages.about-page
  (:require
    [district0x.ui.components.misc :as misc :refer [row row-with-cols col paper page]]
    [name-bazaar.ui.components.app-layout :refer [app-layout]]
    [name-bazaar.ui.components.misc :refer [a]]
    [name-bazaar.ui.styles :as styles]))

(defmethod page :route/about []
  [app-layout
   [paper
    [:h1
     {:style styles/page-headline}
     "About"]
    [:div "Enter text here"]]])