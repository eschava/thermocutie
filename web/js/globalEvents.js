app.run(function($rootScope){
   document.addEventListener("mousedown", onMouseDown, false);
   document.addEventListener("mouseup", onMouseUp, false);
   document.addEventListener("mouseover", onMouseOver, false);
   document.addEventListener("mouseout", onMouseOut, false);

   function onMouseDown() {
       $rootScope.$apply(function(){
           $rootScope.mouseDown = true;
       });
   }

   function onMouseUp() {
       $rootScope.$apply(function(){
           $rootScope.mouseDown = false;
       });
   }

   function onMouseOver(event) {
       $rootScope.$apply(function(){
           $rootScope.mouseOver = true;
       });
   }

   function onMouseOut(event) {
       if (event.toElement == null) {
           $rootScope.$apply(function(){
               $rootScope.mouseOver = false;
           });
       }
   }
});