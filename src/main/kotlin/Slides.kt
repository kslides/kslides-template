import com.kslides.*
import kotlinx.html.*

fun main() {

  kslides {

    output {
      // Write the presentation html files to /docs for Github Pages or netlify.com
      enableFileSystem = true

      // Run locally or on Heroku
      enableHttp = true
    }

    // CSS values assigned here are applied to all the presentations
    css +=
      """
      #githubCorner path { fill: #258BD2; }
      """

    presentationDefault {
      history = true
      transition = Transition.SLIDE
      transitionSpeed = Speed.SLOW
      githubCornerHref = "https://github.com/pambrose/myslides"
      githubCornerTitle = "View presentation source on Github"
      enableMenu = true
      theme = Theme.SOLARIZED
    }

    presentation {
      markdownSlide {
        content {
          """
          # Paul's Slides
          
          * [REST](rest.html)
          """
        }
      }
    }
    presentation {
      path = "rest.html"

      css += """
        .reveal h2 {
          color: red;
        }
      """

      markdownSlide {
        classes = "title-slide"
        content {
          """
          ## HTTP and REST   
          ## Overview  

          ![Firehose](images/DrinkFromFirehose.png)
          """
        }
      }

      markdownSlide {
        content {
          """
          ## Network Elements   
          
          * IP Address: 192.168.123.12 
          
          * Domain Name: www.example.com
          
          * Port: 80, 22, 8080 
                   
          * Packets: TCP, UDP   
          """
        }
      }

      dslSlide {
        content {
          h2 { +"Network Protocols" }
          table {
            tr {
              td { +"TCP" }
              td { +"UDP" }
            }
            tr {
              td { +"Transmission control protocol" }
              td { +"User datagram protocol" }
            }
            tr {
              td { +"connection-oriented" }
              td { +"connectionless" }
            }
            tr {
              td { +"reliable/slow" }
              td { +"not guaranteed/fast" }
            }
          }
        }
      }

      markdownSlide {
        content {
          """
          ## Protocols using UDP  
                    
          * Media streaming (lost frames are ok)
          
          * Games that don't care if you get every update
          
          * Local broadcast mechanisms (different machines "discovering" each other)

          * Tunneling/VPN (lost packets are ok - the tunneled protocol takes care of it)
          """
        }
      }

      markdownSlide {
        content {
          """
          ## Protocols using TCP  
          
          * SSH (22), FTP (21), telnet (23)
          
          * SMTP (25) -- sending mail
          
          * IMAP (143), POP (110) -- receiving mail
          
          * HTTP (80), HTTPS (443)
          """
        }
      }

      markdownSlide {
        content {
          """
          ## HTTP  
          
          * Asymmetric client/server

          * Request/Response
                   
          ![Client/Server](images/client-server.png)
          """
        }
      }

      markdownSlide {
        content {
          """
          ## HTTP Servers
            
          * Static Servers: Apache, IIS, Nginx
          
          * Dynamic Servers: Kotlin, Java, Node.js, Python, etc.
          """
        }
      }

      markdownSlide {
        content {
          """
          ## HTTP Clients
                    
          * Browser Client: Chrome, Firefox, Safari, IE, etc.
          
          * Command Client: httpie, curl, wget, etc.

          * Code Client: Kotlin, Java, JS, Python, etc.
          """
        }
      }

    }
  }
}
