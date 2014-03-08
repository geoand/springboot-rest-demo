function FrameworksModel() {
  var gSobject = gs.inherit(gs.baseClass,'FrameworksModel');
  gSobject.clazz = { name: 'framework.model.FrameworksModel', simpleName: 'FrameworksModel'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.loadFrameworks = function(onLoaded) {
    $.ajax({
          type: "GET",
          url: "/frameworks"
        })
        .done(function( msg ) {
            var list = gs.list(msg);
            onLoaded(list);
        });
  }
  gSobject.addFramework = function(name, url, urlImage, onAdded) {
    $.ajax({
            type: "POST",
            url: "/frameworks",
            data: { name: name, url: url, urlImage: urlImage }
        })
        .done(function( msg ) {
            var framework = Framework();
            gs.passMapToObject(msg, framework);
            onAdded(framework);
        });
  }
  gSobject.FrameworksModel1 = function(map) { gs.passMapToObject(map,this); return this;};
  if (arguments.length==1) {gSobject.FrameworksModel1(arguments[0]); }
  
  return gSobject;
};