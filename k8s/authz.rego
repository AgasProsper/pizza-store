package httpapi.authz

default allow = false

allow {
  input.method == "GET"
  input.path == "/"
  input.headers.authorization == "successful"
}
