class UrlMappings {

	static mappings = {
        "/center/$controller/$action?/$id?(.$format)?"(namespace: 'center')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

//        "/"(view:"/index")
        "/"(controller: 'index', action: 'index')
//        "500"(view:'/error')
        "500"(controller: 'exception', action: 'index')
//        "404"(controller: 'exception', action: 'notFound')
	}
}
