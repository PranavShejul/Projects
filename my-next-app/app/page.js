import Link from 'next/link';
import './styles.css';
export default function Home() {
    return (

        <div>
            <center>
                <h1>Welcome to my portfolio </h1>
                <h3>Pranav Shejul</h3>
            </center>
            <br></br>
            
            <div>
                <center><h3 style={{color: 'blue'}}>My Skills</h3></center>
            </div>
            <br></br>

            <div style={{display:'flex', gap:'10%'}}>
            <div style={{width:'25%'}}></div>
            <div style={{width:'25%',color:'#000',padding:'20px',border:'1px solid black'}}>
                <h3>React</h3><span>React is a JavaScript library used to build fast and interactive user interfaces, especially for web applications.</span>
            </div>
            <div style={{width:'25%',color:'#000',padding:'20px',border:'1px solid black'}}>
                <h3>.net</h3><span>.NET is a software development framework by Microsoft used to build web, desktop, and mobile applications.</span>
            </div>
            <div style={{width:'25%'}}></div>
            </div>
            <br></br>
            <br></br>
            
            <div style={{display:'flex', gap:'10%'}}>
            <div style={{width:'25%'}}></div>
            <div style={{width:'25%',color:'#000',padding:'20px',border:'1px solid black'}}>
                <h3>Python</h3><span>Python is a high-level, easy-to-learn programming language used for web development, 
                    data analysis, artificial intelligence, and automation.</span>
            </div>
            <div style={{width:'25%',color:'#000',padding:'20px',border:'1px solid black'}}>
                 <h3>Java</h3><span>Java is a high-level, object-oriented programming language developed by Sun
                     Microsystems and used to build web, desktop, and mobile applications.</span>
            </div>
            <div style={{width:'25%'}}></div>

            </div>
             <center><button><Link href="/about">Read More</Link></button></center>
            </div>
    );
}