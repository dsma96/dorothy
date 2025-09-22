import * as React from "react";
import {forwardRef} from "react";


const Header = forwardRef<HTMLDivElement, any>((props, ref) => {
    return (
        <a href="/"  rel="noopener noreferrer">
            <img
                src={'./dorothy.png'}
                alt={'Dorothy Hairshop'}
                style={{ width: '100%', maxWidth: '450px', alignSelf: 'center' }}
            />
        </a>
    );
});

export default Header;