/**
=========================================================
* Material Dashboard 2 React - v2.2.0
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard-react
* Copyright 2023 Creative Tim (https://www.creative-tim.com)

Coded by www.creative-tim.com

 =========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*/
import { useContext, useState, useEffect } from "react";

// react-router-dom components
import { Link, useNavigate } from "react-router-dom";

// @mui material components
import Card from "@mui/material/Card";
import Switch from "@mui/material/Switch";
import Grid from "@mui/material/Grid";
import MuiLink from "@mui/material/Link";

// @mui icons
import FacebookIcon from "@mui/icons-material/Facebook";
import GitHubIcon from "@mui/icons-material/GitHub";
import GoogleIcon from "@mui/icons-material/Google";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDInput from "components/MDInput";
import MDButton from "components/MDButton";

// Authentication layout components
import BasicLayout from "layouts/authentication/components/BasicLayout";

// Images
import bgImage from "assets/images/bg-sign-in-basic.jpeg";

// 사용자 정보 저장
import AuthContext from "util/AuthContext";
import CustomSnackBar from "components/MDSnackbar/CustomSnackbar";
import { API_BASE_URL } from "util/host-utils";
import { GoogleLogin, GoogleOAuthProvider } from "@react-oauth/google";
import jwtDecode from "jwt-decode";
import FacebookLogin from "react-facebook-login";

function Basic() {
  const [rememberMe, setRememberMe] = useState(false);
  const [open, setOpen] = useState(false);

  const handleSetRememberMe = () => setRememberMe(!rememberMe);

  const redirection = useNavigate();

  // AuthContext에서 onLogin 함수를 가져옵니다.
  const { onLogin, isLoggedIn } = useContext(AuthContext);

  useEffect(() => {
    if (isLoggedIn) {
      setOpen(true);
      setTimeout(() => {
        redirection("/");
      }, 1000);
    }
  }, [isLoggedIn, redirection]);

  const [userId, setUserId] = useState();
  const [password, setPassword] = useState();
  const [sns, setSns] = useState(0);

  const emailHandler = (e) => {
    setUserId(e.target.value);
  };

  const pwHandler = (e) => {
    setPassword(e.target.value);
  };

  // ip 주소 정보 얻기
  const getIp = async () => {
    let ip;
    await fetch("https://geolocation-db.com/json/", {
      method: "GET",
    })
      .then((res) => res.json())
      .then((json) => {
        ip = json.IPv4;
      });
    // os정보얻기
    const userAgent = window.navigator.userAgent;
    console.log(userAgent);
    console.log(ip);
    return { ip, userAgent };
  };

  // 로그인 요청
  const loginHandler = async () => {
    if (!userId) {
      alert("이메일을 입력하세요");
      return;
    }
    if (!password) {
      alert("비밀번호를 입력하세요");
      return;
    }
    const { ip, userAgent } = await getIp();
    console.log("ip " + ip);
    await fetch(`${API_BASE_URL}/api/v1/signIn`, {
      method: "POST",
      headers: {
        "content-type": "application/json",
        "USER-AGENT": userAgent,
        "X-Forwarded-For": ip,
      },
      body: JSON.stringify({ userId, password }),
    })
      .then((res) => {
        if (res.status === 200) {
          return res.json();
        }
        //잘못된 요청시 경고창 띄움
        if (res.status !== 200) {
          const json = res.json();
          console.log(json);
          if (res.status === 401) {
            alert(json.message);
          } else {
            alert("서버와 통신이 원활하지 않습니다.");
          }
          return;
        }
      })
      .then((json) => {
        console.log(json);
        const { tokenBox, nickName } = json.data;
        alert("로그인 성공했습니다.");
        // localStorage.setItem("ACCESS_TOKEN", accessToken);
        // localStorage.setItem("REFRESH_TOKEN", refreshToken);
        // localStorage.setItem("LOGIN_USERNAME", nickName);
        // 전역함수로 변경
        onLogin(tokenBox, nickName, rememberMe);
        redirection("/");
        return;
      })
      .catch((err) => {
        console.error("에러 ", err);
        alert("서버와 통신이 원활하지 않습니다.");
      });
  };

  const googleLogin = (e) => {
    e.preventDefault();
    setSns(1);
  };

  const facebookLogin = (e) => {
    e.preventDefault();
    setSns(2);
  };

  const responseFacebook = async (response) => {
    console.log(response);
    console.log(response.email);
    // console.log(jwtDecode(response.accessToken));
  };

  return (
    <>
      {!isLoggedIn && (
        <BasicLayout image={bgImage}>
          <Card>
            <MDBox
              variant="gradient"
              bgColor="info"
              borderRadius="lg"
              coloredShadow="info"
              mx={2}
              mt={-8}
              p={2}
              mb={1}
              textAlign="center"
            >
              <MDTypography variant="h4" fontWeight="medium" color="white" mt={1}>
                로그인
              </MDTypography>
              <Grid container spacing={3} justifyContent="center" sx={{ mt: 1, mb: 2 }}>
                <Grid item xs={2}>
                  <MDTypography
                    component={MuiLink}
                    onClick={facebookLogin}
                    href="#"
                    variant="body1"
                    color="white"
                  >
                    <FacebookIcon color="inherit" />
                  </MDTypography>
                </Grid>
                <Grid item xs={2}>
                  <MDTypography component={MuiLink} href="#" variant="body1" color="white">
                    <GitHubIcon color="inherit" />
                  </MDTypography>
                </Grid>
                <Grid item xs={2}>
                  <MDTypography
                    component={MuiLink}
                    onClick={googleLogin}
                    href="#"
                    variant="body1"
                    color="white"
                  >
                    <GoogleIcon color="inherit" />
                  </MDTypography>
                </Grid>
              </Grid>
            </MDBox>
            <MDBox pt={4} pb={3} px={3}>
              {sns === 0 && (
                <MDBox component="form" role="form">
                  <MDBox mb={2}>
                    <MDInput type="email" label="Email" fullWidth onChange={emailHandler} />
                  </MDBox>
                  <MDBox mb={2}>
                    <MDInput type="password" label="Password" fullWidth onChange={pwHandler} />
                  </MDBox>
                  <MDBox display="flex" alignItems="center" ml={-1}>
                    <Switch checked={rememberMe} onChange={handleSetRememberMe} />
                    <MDTypography
                      variant="button"
                      fontWeight="regular"
                      color="text"
                      onClick={handleSetRememberMe}
                      sx={{ cursor: "pointer", userSelect: "none", ml: -1 }}
                    >
                      &nbsp;&nbsp;저장하기
                    </MDTypography>
                  </MDBox>
                  <MDBox mt={4} mb={1}>
                    <MDButton variant="gradient" color="info" fullWidth onClick={loginHandler}>
                      로그인
                    </MDButton>
                  </MDBox>
                </MDBox>
              )}
              {sns === 1 && (
                <MDBox display="flex" justifyContent="center" alignItems="center" mx={3}>
                  <MDBox mt={3} mb={1} textAlign="center">
                    <GoogleOAuthProvider
                      clientId={
                        "1085513147099-07sehglg71sajoag7bqbpovb49rivhpn.apps.googleusercontent.com"
                      }
                      onScriptLoadError={() => console.log("실패")}
                      onScriptLoadSuccess={() => console.log("성공")}
                    >
                      <GoogleLogin
                        size="large"
                        width={1}
                        onSuccess={(res) => {
                          console.log(jwtDecode(res.credential));
                        }}
                        onError={(err) => {
                          console.log(err);
                        }}
                      />
                    </GoogleOAuthProvider>
                  </MDBox>
                </MDBox>
              )}

              {sns === 2 && (
                <MDBox display="flex" justifyContent="center" alignItems="center" mx={3}>
                  <MDBox mt={3} mb={1} textAlign="center">
                    <FacebookLogin
                      appId={"272051182316402"}
                      autoLoad={true}
                      scope="email"
                      returnScopes="true"
                      fields="name,email,picture"
                      textButton="페이스북으로 로그인"
                      callback={responseFacebook}
                    />
                  </MDBox>
                </MDBox>
              )}

              <MDBox mt={3} mb={1} textAlign="center">
                {sns !== 0 && (
                  <MDTypography
                    component={Link}
                    onClick={() => setSns(0)}
                    variant="button"
                    color="info"
                    fontWeight="medium"
                    textGradient
                  >
                    회원으로 로그인
                  </MDTypography>
                )}
                <br />
                <MDTypography variant="button" color="text">
                  회원이 아니신가요?{" "}
                  <MDTypography
                    component={Link}
                    to="/authentication/sign-up"
                    variant="button"
                    color="info"
                    fontWeight="medium"
                    textGradient
                  >
                    회원가입하기
                  </MDTypography>
                </MDTypography>
              </MDBox>
            </MDBox>
          </Card>
        </BasicLayout>
      )}{" "}
      <CustomSnackBar open={open} />
    </>
  );
}

export default Basic;
