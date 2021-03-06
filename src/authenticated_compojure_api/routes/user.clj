(ns authenticated-compojure-api.routes.user
  (:require [authenticated-compojure-api.middleware.cors :refer [cors-mw]]
            [authenticated-compojure-api.middleware.token-auth :refer [token-auth-mw]]
            [authenticated-compojure-api.middleware.authenticated :refer [authenticated-mw]]
            [authenticated-compojure-api.route-functions.user.create-user :refer [create-user-response]]
            [authenticated-compojure-api.route-functions.user.delete-user :refer [delete-user-response]]
            [authenticated-compojure-api.route-functions.user.modify-user :refer [modify-user-response]]
            [compojure.api.sweet :refer :all]))


(def user-routes
  "Specify routes for User functions"
  (context "/api/user" []

    (POST "/"           {:as request}
           :tags        ["User"]
           :return      {:username String}
           :middleware  [cors-mw]
           :body-params [email :- String username :- String password :- String]
           :summary     "Create a new user with provided username, email and password."
           (create-user-response email username password))

     (DELETE "/:id"        {:as request}
              :tags        ["User"]
              :path-params [id :- Long]
              :return      {:message String}
              :middleware  [token-auth-mw cors-mw authenticated-mw]
              :summary     "Deletes the specified user. Requires token to have `admin` auth or self ID."
              :description "Authorization header expects the following format 'Token {token}'"
              (delete-user-response request id))

     (PATCH  "/:id"          {:as request}
              :tags          ["User"]
              :path-params   [id :- Long]
              :body-params   [{username :- String ""} {password :- String ""} {email :- String ""}]
              :header-params [authorization :- String]
              :return        {:id Long :email String :username String}
              :middleware    [token-auth-mw cors-mw authenticated-mw]
              :summary       "Update some or all fields of a specified user. Requires token to have `admin` auth or self ID."
              :description   "Authorization header expects the following format 'Token {token}'"
              (modify-user-response request id username password email))))
