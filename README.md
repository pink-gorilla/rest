# rest [![GitHub Actions status |pink-gorilla/rest](https://github.com/pink-gorilla/rest/workflows/CI/badge.svg)](https://github.com/pink-gorilla/rest/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/rest.svg)](https://clojars.org/org.pinkgorilla/rest)

**End Users** this project is not for you.


## Common REST Apis
- google
- woo commerce
- github
- xero

## Email Sending


# demo

The demo uses the extension manager from goldly to add oauth2 to goldly.

```
cd demo
clj -X:demo:npm-install
clj -X:demo:compile
clj -X:demo
```

Test local user/password login: user: "demo" password: "1234"

Authorize xero/google/...

Then you can run the cli demos that use the access tokens:

```
clj -X:run:rest-google
clj -X:run:rest-github
clj -X:run:rest-xero

clj -X:run:rest-woo

clj -X:run:rest-email


; infer schema from rest-api-response (useful to quickly create api schema)
clj -X:run:rest-schema


```