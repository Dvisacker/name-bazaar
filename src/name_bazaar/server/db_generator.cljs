(ns name-bazaar.server.db-generator
  (:require
    [cljs-time.coerce :refer [to-epoch]]
    [cljs-time.core :as t]
    [cljs-web3.core :as web3]
    [cljs.core.async :refer [<! >! chan alts! timeout]]
    [district0x.server.state :as state]
    [district0x.server.utils :refer [watch-event-once]]
    [district0x.shared.utils :refer [rand-str rand-nth-except]]
    [name-bazaar.server.contracts-api.auction-offering :as auction-offering]
    [name-bazaar.server.contracts-api.auction-offering-factory :as auction-offering-factory]
    [name-bazaar.server.contracts-api.buy-now-offering :as buy-now-offering]
    [name-bazaar.server.contracts-api.buy-now-offering-factory :as buy-now-offering-factory]
    [name-bazaar.server.contracts-api.ens :as ens]
    [name-bazaar.server.contracts-api.mock-registrar :as registrar]
    [name-bazaar.server.contracts-api.offering-registry :as offering-registry]
    [name-bazaar.server.contracts-api.offering-requests :as offering-requests]
    [district0x.shared.utils :as d0x-shared-utils])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def namehash (aget (js/require "eth-ens-namehash") "hash"))
(def normalize (aget (js/require "eth-ens-namehash") "normalize"))
(def sha3 (comp (partial str "0x") (aget (js/require "js-sha3") "keccak_256")))

(def names-per-account 10)

(defn generate! [server-state {:keys [:total-accounts]}]
  (let [ch (chan)]
    (go
      (dotimes [address-index total-accounts]
        (dotimes [_ names-per-account]
          (let [owner (state/my-address server-state address-index)
                label (normalize (rand-str 5))
                name (str label "." registrar/root-node)
                node (namehash name)
                ;offering-type :auction-offering
                offering-type (rand-nth [:buy-now-offering :auction-offering])
                price (web3/to-wei (/ (inc (rand-int 10)) 10) :ether)
                buyer (rand-nth-except owner (state/my-addresses server-state))
                request-name (if (zero? (rand-int 2)) name (normalize (str (rand-str 1)
                                                                           "."
                                                                           registrar/root-node)))]

            (<! (registrar/register! server-state {:ens.record/label label} {:from owner}))

            (<! (offering-requests/add-request! server-state {:offering-request/name request-name} {:form owner}))

            (if (= offering-type :buy-now-offering)
              (<! (buy-now-offering-factory/create-offering! server-state
                                                             {:offering/name name
                                                              :offering/price price}
                                                             {:from owner}))
              (<! (auction-offering-factory/create-offering!
                    server-state
                    {:offering/name name
                     :offering/price price
                     :auction-offering/end-time (to-epoch (t/plus (t/now) (t/weeks 2)))
                     :auction-offering/extension-duration (rand-int 10000)
                     :auction-offering/min-bid-increase (web3/to-wei 0.1 :ether)}
                    {:from owner})))


            (let [[[_ {{:keys [:offering]} :args}]] (alts! [(offering-registry/on-offering-added-once server-state
                                                                                                      {:node node
                                                                                                       :owner owner})
                                                            (timeout 1000)])]
              (if offering
                (do
                  (<! (registrar/transfer! server-state
                                           {:ens.record/label label :ens.record/owner offering}
                                           {:from owner}))

                  (when (= offering-type :auction-offering)
                    (<! (auction-offering/bid! server-state {:offering/address offering} {:value price :from buyer})))

                  #_ (when true #_(zero? (rand-int 2))
                    (if (= offering-type :buy-now-offering)
                      (buy-now-offering/buy! server-state {:offering/address offering} {:value price :from buyer})
                      (auction-offering/bid! server-state {:offering/address offering} {:value price :from buyer}))))

                (.error js/console "Offering for" label "wasn't created"))))))
      (>! ch true))
    ch))
