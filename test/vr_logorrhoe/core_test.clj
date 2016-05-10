;; Freedom patch
(in-ns 'vr-logorrhoe.config)
(def user-home "/tmp")
(def config-directory (utils/conj-path user-home
                                         (str "." app-name)))
(def config-file (utils/conj-path config-directory
                                  "settings.edn"))


(ns vr-logorrhoe.core-test
  (:require [clojure.test :refer :all]
            [vr-logorrhoe
             [config :as config]
             [utils :as utils]]))

(defn- remove-fs-fixtures []
  (utils/remove-folder "/tmp/spectest")
  (utils/remove-folder config/config-directory true))

(defn test-fixtures [f]
  (remove-fs-fixtures)
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
    (utils/remove-folder "/tmp/spectest")
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
    (is (= (utils/folder-exists? config/user-home)
           true))
    (is (= (utils/folder-exists? config/config-directory)
           false))

    (@#'config/write-default-config-file)

    (is (= (utils/folder-exists? config/config-directory)
           true))))
