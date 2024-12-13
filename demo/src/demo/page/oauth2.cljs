(ns demo.page.oauth2
  (:require
   ;[token.identity.user :refer [user-a]]
   [token.identity.ui :refer [user-icon-with-login show-login-dialog]]
   [token.oauth2.store.ui :refer [provider-status-grid]]
   ))


(def provider-scopes
  {:github [;https://docs.github.com/en/developers/apps/scopes-for-oauth-apps
            "user"
             ;"user:email" ; this includes ONLY the user email
            "gist"
            "repo"
            "openid"]
   :google [;https://developers.google.com/identity/protocols/oauth2/scopes
                                            ;"offline_access" ; does not work!
            "openid" ; The scope parameter must begin with the openid value and then include the profile value, the email value, or both.
            "email" ; If the email scope value is present, the ID token includes email and email_verified claims.
            "profile" ; he profile scope value is present, the ID token might (but is not guaranteed to) include the user's default profile claims.
            "https://www.googleapis.com/auth/userinfo.email"
                                            ; readonly 
            "https://www.googleapis.com/auth/spreadsheets.readonly"
            "https://www.googleapis.com/auth/drive.readonly"
            "https://www.googleapis.com/auth/gmail.readonly"
            "https://www.googleapis.com/auth/drive.photos.readonly"
                                            ; edit
            "https://www.googleapis.com/auth/spreadsheets"
            "https://www.googleapis.com/auth/calendar"
            "https://www.googleapis.com/auth/drive"
            "https://www.googleapis.com/auth/drive.appdata"
            "https://www.googleapis.com/auth/drive.file"
            "https://www.googleapis.com/auth/drive.metadata"
            "https://www.googleapis.com/auth/drive.metadata.readonly"
            "https://www.googleapis.com/auth/cloud-platform"

            "https://docs.google.com/feeds/"
            "https://spreadsheets.google.com/feeds"

            "https://www.googleapis.com/auth/cloud_search"]
   :xero ["offline_access" ; refresh_token
          "openid"
          "profile"
          "email"
          "accounting.settings"
          "accounting.reports.read"
          "accounting.journals.read"
          "accounting.contacts"
          "accounting.attachments"
          "accounting.transactions"
          "accounting.transactions.read"]
    ; :woo ["read" "write"  "read_write"]
   })


(defn page-oauth2 [_]
  [:div.w-screen.h-screen
    ;[link-fn #(show-login-dialog) "show login dialog2"]
    ;[user-icon-with-login]
    [provider-status-grid provider-scopes] 
       ])