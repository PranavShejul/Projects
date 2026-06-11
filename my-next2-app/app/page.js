import Image from "next/image";
import Link from "next/link";


export default function Banner() {
  return (
    <div>
    <div style={styles.banner}>
      <span style={{color:'#000'}}>
        Don’t miss the launch event that changes enterprise AI forever. Tune in February 26 at 10 am PT.
      </span>

      <button style={styles.button1}>
        Add to Calendar
      </button>
    </div>
    <br></br>
    <div style={{display:'flex', gap:'10%', padding: '80px 120px'}}>
            <div style={{width:'50%',color:'#63DF4E'}}>
                <h1 style={{
                    fontWeight: "800",
                    fontSize: "64px"
                    }}>
                    AI you can trust.
                    </h1>

                <h1 style={{...styles.h1, color: '#fff'}}>Outcomes you control.</h1>
            </div>
            <div style={{color:'#fff',fontWeight: "300",fontSize: "22px",width:'50%'}}>
                <h4>ServiceNow® AI Control Tower discovers and governs every AI in your enterprise, first
                     or third party. Get a single view to align strategy, manage risk, and drive Al resu
                    lts with confidence.</h4>
                    <br></br>
                    <button style={styles.button2}>
                        See AI control tower
                    </button>                    
                    <button style={{ ...styles.button3, marginLeft: "15px" }}>
                        Explore Platform
                    </button>
            </div>
            <div style={{display:"flex"}}>
                

            </div>
    </div>

    
    <br>
    </br>
     <div style={styles.container1}>
        
        {/* Left Image */}
        <img
        src="/images/Left.png"
        style={styles.leftImage}
        />


        {/* Main Image */}
        <img
        src="/images/Main.png"
        style={styles.mainImage}
        />


        {/* Right Image */}
        <img
        src="/images/right.png"
        style={styles.rightImage}
        />
        </div>

        <div style={{width:"100%",padding:"125px 0px"}}>
           <center> <span style={{fontSize:"38px",fontWeight:"700"}}>The world works with ServiceNow</span></center>

            <div style={{...styles.container2, padding:"60px 0px"}}>
        
        {/* Left Image */}
        <img
        src="/images/adobe.svg"
        style={styles.adobeImage}
        />


        {/* Main Image */}
        <img
        src="/images/uber.svg"
        style={styles.uberImage}
        />


        {/* Right Image */}
        <img
        src="/images/pepsico.avif"
        style={styles.pepsicoImage}
        />

        {/* Main Image */}
        <img
        src="/images/visa.svg"
        style={styles.visaImage}
        />
        
        {/* Main Image */}
        <img
        src="/images/astrazeneca.svg"
        style={styles.astrazenecaImage}
        />
        
        {/* Main Image */}
        <img
        src="/images/lenovo.svg"
        style={styles.lenovoImage}
        />
        </div>

           <center> <span style={{fontSize:"48px",fontWeight:"700",color:"#63df4e"}}>ServiceNow is the AI control tower</span></center>
           <div style={{ textAlign:"center", marginBottom:"120px" }}>
  <span style={{fontSize:"48px",fontWeight:"700"}}>
    for business reinvention
  </span>
</div>


    <div style={styles.container3}>

      <div style={styles.imageWrapper}>

        <Image
          src="/images/infinity.png"
          alt="Infinity Banner"
          fill
          style={{ objectFit: "contain" }}
        />

      </div>
    </div>
  </div>

    <div style={{...styles.wrapper, marginTop:"120px"}}>
      <div style={styles.container4}>
        <div style={{ ...styles.tab, ...styles.activeTab }}>IT</div>
        <div style={styles.tab}>CRM</div>
        <div style={styles.tab}>Employee Experience</div>
        <div style={styles.tab}>Risk and Security</div>
        <div style={styles.tab}>App Development</div>
      </div>
    </div>
    <div>
  </div>
    <div style={{display:'flex', gap:'10%', padding: '80px 120px'}}>
            <div style={{width:'50%',color:'#fff'}}>
                <h1 style={{
                    fontWeight: "800",
                    fontSize:"48px"
                    }}>
                    The best incident is the one that never happens
                    </h1>

                <span style={{fontSize: "22px", color: '#fff'}}>Shift from reactive management to Autonomous
                   IT. Anticipate, resolve, and secure issues before they disrupt the business.
                </span>
                <button style={{...styles.button2, marginTop:'20PX', marginBottom: '20PX'}}>
                        Build Autonomous IT
                    </button>
                    <br>
                    </br>
                    <Link href="/IT Service Management" style={{display: "block", marginTop: "20px"}}>IT Service Management
                    </Link>
                    <Link href="/IT Operations Management" style={{display: "block", marginTop: "20px"}}>IT Operations Management
                    </Link>
                    <Link href="/IT Asset management" style={{display: "block", marginTop: "20px"}}>IT Asset management
                    </Link>
                    <Link href="/Strategic Portfolio Management" style={{display: "block", marginTop: "20px"}}>Strategic Portfolio Management
                    </Link>
              <div style={{color:'#fff',fontWeight: "300",fontSize: "22px",width:'50%'}}></div>
            </div>
      </div>
</div>
  );
}


const styles = {
    body:  {
  fontFamily: "Poppins, sans-serif",

    },
    banner: {
        background: "linear-gradient(to bottom, #58DA65, #24C2CE)",
        padding: "12px 20px",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        gap: "20px",
    },

    button1: {
        
      fontWeight: "600",
        border: "1px solid black",
        padding: "6px 14px",
        borderRadius: "20px",
        background: "transparent",
        cursor: "pointer",
        color: "#000",
    },

    button2: {
        
      fontWeight: "600",
        border: "1px solid black",
        padding: "6px 14px",
        borderRadius: "20px",
        background: "#58DA65",
        cursor: "pointer",
        color: "#053042",
    },

    button3: {
      fontWeight: "600",
        border: "1px solid green",
        padding: "6px 14px",
        borderRadius: "20px",
        background: "transparent",
        cursor: "pointer",
        color: "#fff",
    },
    
    button2: {
      fontWeight: "600",
        border: "1px solid black",
        padding: "15px 20px",
        borderRadius: "20px",
        background: "#58DA65",
        cursor: "pointer",
        color: "#053042",
    },

    h1: {
        fontWeight: "800",
        fontSize: "64px",
    },

    h2: {
        fontSize: "46px",
    },

    h3: {
        fontSize: "36px",
    
    },

    h4: {
        fontSize:"26px",
        fontWeight: "bold",
        color: "#E6EDEE",           // light text color
        padding: "10px 20px",
        margin: "0",
    },

    greenText: {
      fontWeight: "800",
      color: "#5CF04A", // bright green
      margin: "0",
      lineHeight: "1.1",
    },

    whiteText: {
      fontWeight: "800",
      color: "#E6EDEE", // white
      margin: "0",
      lineHeight: "1.1",
    },

    container1: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "30px",
    background: "transparent",
  },

  leftImage: {
    width: "400px",
    borderRadius: "20px"
  },

  mainImage: {
    width: "600px",
    borderRadius: "30px"
  },

  rightImage: {
    width: "400px",
    borderRadius: "20px"
  },

  adobeImage: {
    width: "100px",
    height: "100px",
    borderRadius: "20px"
  },
  
  astrazenecaImage: {
    width: "100px",
    height: "100px",
    borderRadius: "20px"
  },
  
  pepsicoImage: {
    width: "100px",
    height: "100px",
    borderRadius: "20px"
  },
  
  uberImage: {
    width: "100px",
    height: "100px",
    borderRadius: "20px"
  },
  
  visaImage: {
    width: "100px",
    height: "100px",
    borderRadius: "20px"
  },
  
  lenovoImage: {
    width: "100px",
    height: "100px",
    borderRadius: "20px"
  },

    container2: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "30px",
    background: "transparent",
    },

    container3: {
  padding:"50px 0px",
  background: "transparent",
  height: "500px",
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
},

imageWrapper: {
  position: "relative",
  width: "1500px",
  height: "800px",
},
 wrapper: {
    padding: "20px",
    display: "flex",
    justifyContent: "center",
  },

  container4: {
  background: "transparent",
    display: "flex",
    gap: "100px",
    alignItems: "center",
    background: "rgba(255,255,255,0.05)",
    padding: "10px 20px",
    borderRadius: "40px",
  },

  tab: {
    color: "#e0e0e0",
    fontSize: "18px",
    fontWeight: "500",
    cursor: "pointer",
  },

  activeTab: {
    background: "#d9d9d9",
    color: "#000",
    padding: "20px 100px",
    borderRadius: "30px",
  },
  
};
