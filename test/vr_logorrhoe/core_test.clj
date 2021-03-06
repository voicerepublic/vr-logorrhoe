(ns vr-logorrhoe.core-test
  (:require [clojure.test :refer :all]
            [vr-logorrhoe
             [config :as config]
             [utils :as utils]]))

;; Freedom patch
(in-ns 'vr-logorrhoe.config)
(def app-directory "/tmp/.vr-logorrhoe/")
(def config-file-path "/tmp/.vr-logorrhoe/settings.edn")
(in-ns 'vr-logorrhoe.core-test)
;; End Freedom patch

(defn- remove-fs-fixtures []
  (utils/delete-folder "/tmp/spectest")
  (utils/delete-folder config/app-directory true))

(defn test-fixtures [f]
  (remove-fs-fixtures)
  (utils/set-logger-path "/tmp/messages_spec.log")
  (f)
  (remove-fs-fixtures))

(use-fixtures :once test-fixtures)


(deftest stream-helpers
  (testing "print an input-stream does not throw an exception"
    (spit "/tmp/spec_test1" "123\n321")
    (utils/print-input-stream
     (new java.io.FileInputStream "/tmp/spec_test1")))

(deftest utils-functions
  (testing "Conj folder names"
    (is (= (utils/conj-path "/tmp" "spectest")
           "/tmp/spectest")))

  (testing "Remove folder"
    (utils/create-folder "/tmp/spectest")
    (utils/delete-folder "/tmp/spectest")
    (is (= (utils/folder-exists? "/tmp/spectest")
           false)))

  (testing "Create folder"
    (is (= (utils/folder-exists? "/tmp/spectest")
           false))
    (utils/create-folder "/tmp/spectest")
    (is (= (utils/folder-exists? "/tmp/spectest")
           true)))

  (testing "Folder exists?"
    (is (= (utils/folder-exists? "/tmp")
           true))
    (is (= (utils/folder-exists? "/made-up")
           false)))))

(deftest initial-configuration-setup
  (testing "Sets up a config dir if not yet existing"
    (is (= (utils/folder-exists? config/user-home-path)
           true))
    (is (= (utils/folder-exists? config/app-directory)
           false))

    (@#'config/write-default-config-file)

    (is (= (utils/folder-exists? config/app-directory)
           true))))
