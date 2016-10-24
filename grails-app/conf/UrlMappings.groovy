class UrlMappings {

	static mappings = {
        "/user/$controller/$action?/$id?(.$format)?"(namespace: 'user')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
//        "500"(view:'/error')
        "500"(controller: 'exception', action: 'index')
        "404"(controller: 'exception', action: 'notFound')
	}
}
