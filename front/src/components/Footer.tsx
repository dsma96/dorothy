import * as React from 'react';
import BottomNavigation from "@mui/material/BottomNavigation";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import HomeIcon from "@mui/icons-material/Home";
import AccountBoxIcon from "@mui/icons-material/AccountBox";
import {styled} from "@mui/material/styles";
import BottomNavigationAction from "@mui/material/BottomNavigationAction";
import {useNavigate} from "react-router";
import { Menu, MenuItem } from '@mui/material';
import {Member} from "../typedef";
import {useSelector} from "react-redux";
import {Textsms} from "@mui/icons-material";


interface FooterProps{
    backUrl: string; // back button url
    showMyInfo?: boolean;
    showMyStamp?:boolean;
    style?: React.CSSProperties;
}

const TabBarButton = styled(BottomNavigationAction)({
    color: '#e67e22',
    '.Mui-selected, svg':{
        color: '#e67e22',
    }
});


export default function  Footer({backUrl, showMyInfo, showMyStamp,style}: FooterProps) {
    const loginUser: Member = useSelector( state => state.user.loginUser);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        
        setAnchorEl(null);
    };
    const navigate = useNavigate();
    return(
        <BottomNavigation
            showLabels={true}
            className='stickToBottom'
            style= {{...style}}

        >
            <TabBarButton label="My Info" color='primary' icon={<AccountBoxIcon/>} onClick={ handleClick} />
            <Menu
                id="demo-positioned-menu"
                aria-labelledby="demo-positioned-button"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                }}
            >
                {/*{showMyStamp == false ? null : <MenuItem onClick={()=>navigate("/stamp")}>My Stamp</MenuItem>}*/}
                {showMyInfo == false ? null :<MenuItem onClick={()=>navigate("/my")}>My Info</MenuItem>}
                {loginUser.rootUser == true ? <MenuItem onClick={()=>navigate("/memberList")}>Member List</MenuItem> : null}
                {loginUser.rootUser == true ? <MenuItem onClick={()=>navigate("/stat")}>Statistics</MenuItem> : null}
            </Menu>
            <TabBarButton label="Contact" color='primary' icon={<Textsms/>} onClick={() => window.location.href=`sms:6475008282`}/>
            <TabBarButton label="Back" color='primary' icon={<ArrowBackIosIcon/>} onClick={() => navigate("BACK" == backUrl ? -1 : backUrl)}/>
        </BottomNavigation>
    )
}

