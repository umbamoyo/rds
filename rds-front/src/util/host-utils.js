// 브라우저에서 현재 클라이언트의 호스트 이름 얻어오기
const clientHostName = window.location.hostname;

let backEndHostName; // 백엔드 서버 호스트 이름

if (clientHostName === "localhost") {
  //개발 중
  backEndHostName = "http://localhost:8080";
} else if (clientHostName === "tramovel.com") {
  //배포해서 서비스 중
  backEndHostName = "http://13.209.179.148";
}

export const API_BASE_URL = backEndHostName;
export const USER = "/user";
