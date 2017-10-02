(ns name-bazaar.ui.components.app-layout
  (:require
    [district0x.ui.components.active-address-balance :refer [active-address-balance]]
    [district0x.ui.components.active-address-select :refer [active-address-select]]
    [district0x.ui.components.transaction-log :refer [transaction-log]]
    [district0x.ui.utils :refer [current-component-mui-theme]]
    [name-bazaar.ui.components.app-bar-search :refer [app-bar-search]]
    [name-bazaar.ui.constants :as constants]
    [name-bazaar.ui.styles :as styles]
    [name-bazaar.ui.utils :refer [offerings-newest-url offerings-most-active-url offerings-ending-soon-url path-for]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    [soda-ash.core :as ui]))

(defn side-nav-menu-logo []
  [:a.side-nav-logo
   {:href (path-for :route/home)}
   [:img
    {:src "./images/logo@2x.png"}]])

(defn district0x-banner []
  [:div.district0x-banner
   [:div.logo ""]
   [:div "Part of the"]
   [:a
    {:href "https://district0x.io"
     :target :_blank}
    "district0x Network"]])

(def nav-menu-items-props [{:text "Offerings"
                            :route :route.offerings/search
                            :class :offerings-search}
                           {:text "Latest"
                            :href offerings-newest-url
                            :class "nested-item offerings-newest"}
                           {:text "Most Active"
                            :href offerings-most-active-url
                            :class "nested-item offerings-most-active"}
                           {:text "Ending Soon"
                            :href offerings-ending-soon-url
                            :class "nested-item offerings-ending-soon"}
                           {:text "Requests"
                            :route :route.offering-requests/search
                            :class :offering-requests-search}
                           {:text "Watched Names"
                            :route :route/watched-names
                            :class :watched-names}
                           {:text "Create Offering"
                            :route :route.offerings/create
                            :class :create-offering}
                           {:text "My Offerings"
                            :route :route.user/my-offerings
                            :class :my-offerings}
                           {:text "My Purchases"
                            :route :route.user/my-purchases
                            :class :my-purchases}
                           {:text "My Bids"
                            :route :route.user/my-bids
                            :class :my-bids}
                           {:text "My Settings"
                            :route :route.user/my-settings
                            :class :my-settings}
                           {:text "Register Name"
                            :route :route.mock-registrar/register
                            :class :register-name}
                           {:text "How it works"
                            :route :route/how-it-works
                            :class :how-it-works}
                           {:text "About"
                            :route :route/about
                            :class :about}])

(defn app-bar []
  [ui/Grid
   {:class :app-bar
    :columns 3
    :vertical-align :middle
    :padded true}
   [ui/GridColumn
    {:class :left-section
     :widescreen 2
     :large-screen 4
     :computer 4
     :tablet 1
     :mobile 2
     :vertical-align :middle}
    [active-address-select]
    [:div.sidebar-toggle
     {:on-click (fn [e]
                  (dispatch [:district0x.menu-drawer/set true])
                  (.stopPropagation e))}]]
   [ui/GridColumn
    {:class :middle-section
     :widescreen 12
     :large-screen 8
     :computer 8
     :tablet 15
     :mobile 14
     :text-align :center
     :vertical-align :middle}
    [app-bar-search]]
   [ui/GridColumn
    {:class :right-section
     :widescreen 2
     :large-screen 4
     :computer 4
     :only "computer"
     :text-align :center
     :vertical-align :middle}
    [active-address-balance]]])

(defn app-layout []
  (let [drawer-open? (subscribe [:district0x/menu-drawer-open?])
        min-computer-screen? (subscribe [:district0x.screen-size/min-computer-screen?])
        active-page (subscribe [:district0x/active-page])]
    (fn [& children]
      [ui/SidebarPushable
       {:class "app-container"}
       [ui/Sidebar
        {:as (aget js/semanticUIReact "Menu")
         :visible (or @drawer-open? @min-computer-screen?)
         :animation "overlay"
         :vertical true
         :inverted true}
        [side-nav-menu-logo]
        (doall
          (for [{:keys [:text :route :href :class]} nav-menu-items-props]
            (let [href (or href (path-for route))]
              [ui/MenuItem
               {:key text
                :as "a"
                :href href
                :class class
                :active (= (str "#" (:path @active-page)) href)}
               text])))
        [district0x-banner]]
       [ui/SidebarPusher
        {:on-click (fn []
                     (when-not @min-computer-screen?
                       (dispatch [:district0x.menu-drawer/set false])))}
        [app-bar]
        [ui/Grid
         {:columns 1
          :centered true}
         (into [ui/GridColumn
                {:widescreen 8
                 :large-screen 12
                 :tablet 14
                 :mobile 15}]
               children)]]])))