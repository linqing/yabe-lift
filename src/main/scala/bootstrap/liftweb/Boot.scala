package bootstrap.liftweb

import net.liftweb._
import net.liftweb.http._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, Post)

    // where to search snippet
    LiftRules.addToPackages("code")

    val IfLoggedIn = If(() => User.loggedIn_?,
			()=>RedirectResponse("/login"))
      
    def menus = List(
    		Menu.i("Home") / "index" >> User.AddUserMenusAfter,
    		//Menu.i("admin") / "admin" / ** >> IfLoggedIn
    		Menu.i("My posts") / "admin" / "posts" / ** >> IfLoggedIn  >> LocGroup("admin"),
    		Menu.i("Posts") / "admin" / "all_posts" >> IfLoggedIn >> LocGroup("admin"),
		    Menu.i("Tags") / "admin" / "tags" >> IfLoggedIn >> LocGroup("admin"),
		    Menu.i("Comments") / "admin" / "comments" >> IfLoggedIn >> LocGroup("admin"),
		    Menu.i("Users") / "admin" / "users" / ** >> IfLoggedIn >> LocGroup("admin")
		    //Menu.i("User / add") / "admin" / "useradd" >> IfLoggedIn
		    ):::Post.menus
    
    // Build SiteMap
    def sitemap = SiteMap(
      menus:_*
      //Menu.i("Home") / "index" >> User.AddUserMenusAfter/*, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      // Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
      //				 "Static Content")) */
    )

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))
    
    //Rewrite
    LiftRules.statelessRewrite.append{
      case RewriteRequest(ParsePath("login"::Nil,_,_,_),_,_)=>
	RewriteResponse("user_mgt"::"login"::Nil)
      case RewriteRequest(ParsePath("logout"::Nil,_,_,_),_,_)=>
	RewriteResponse("user_mgt"::"logout"::Nil)
    }
    
    // Use jQuery 1.4
    //LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

    //Show the spinny image when an Ajax call starts
    //LiftRules.ajaxStart =
    //  Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    //LiftRules.ajaxEnd =
    //  Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    //LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}
