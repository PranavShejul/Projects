import Link from 'next/link';

export default function About() {
    return (
        <center><div>
            <h1> About Page </h1>
            <br></br>

            <div>
            <h3>Pranav Shejul </h3>
            </div>
            <br></br>
            <p style={{width: '75%'}}>This website is developed to provide information about languages learned by me.
                It is built using Next.js and demonstrates modern web development
                features like routing and responsive design.</p>
                <br></br>
             <button><Link href="/">Go back to home Page</Link></button>
        </div>
        </center>
    );
}