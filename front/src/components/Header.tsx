import * as React from "react";


export default function  Header() {
    return (
        <a href="/"  rel="noopener noreferrer">
            <img
                src={'./dorothy.png'}
                alt={'Dorothy Hairshop'}
                style={{ width: '100%', maxWidth: '450px', alignSelf: 'center' }}
            />
        </a>
    );
}
