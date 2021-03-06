(ns server.district0x.decryption-tests
  (:require [cljs.test :refer [deftest is testing run-tests]]
            [district0x.server.state :as state]
            [district0x.shared.encryption-utils :as encryption-utils]))

(deftest decryption-tests
  
  (testing "content decryption test."
    (let [keypair (select-keys state/default-config [:public-key :private-key])
          content "test@district0x.io"
          base64-encrypted-content (->> content
                                        (encryption-utils/encrypt (:public-key keypair)) 
                                        (encryption-utils/encode-base64))
          decoded-content (->> base64-encrypted-content
                               (encryption-utils/decode-base64)
                               (encryption-utils/decrypt (:private-key keypair)))]
      (is (= content decoded-content)))))

