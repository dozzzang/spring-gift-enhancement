# spring-gift-product
***
## Step 1 : API 명세(RestController)
| 기능    | Method | URL                       |request|response| 상태코드     |
|-------|-------|---------------------------|----|----|----------|
| 상품 조회 | GET| /api/products/{productId} |요청 param|상품 정보| 200:정상조회 |
| 상품 추가 |POST| /api/products             |요청 body|등록 정보| 201:정상추가 |
| 상품 수정 |PUT| /api/products/{productId} |요청 body|수정 정보| 200:정상수정 |
| 상품 삭제 |DELETE| /api/products/{productId} |요청 param|X| 204:정상삭제 |

## Step 2 : View Controller 명세
| 기능    | Method | URL            |응답 페이지   |
|-------|-------|------------------|----------|
| 상품 목록 조회 | GET| / || index.html|
| 상품 단건 조회 |GET| /products/{id} |detail.html |
| 상품 추가 |GET+POST| /product/news + /products      |form.html + redirect:/|
| 상품 수정 |GET+POST| /products/{id} |update.html + redirect:/products/{id} |
| 상품 삭제 |GET| /products/{id}/delete | redirect:/ |

## Step 3 : H2 DB
- H2 DB 사용 설정
- Product schema 및 data.sql 설정
- 상품정보 : 자바 컬렉션 기반 -> H2 DB (Repository 구현)

# spring-gift-wishlist

## Step 1 : 유효성 검사 및 예외 처리
### 요구사항 분석
- 상품을 추가하거나 수정하는 경우, 클라이언트로부터 잘못된 값이 전달될 수 있다.
  잘못된 값이 전달되면 클라이언트가 어떤 부분이 왜 잘못되었는지 인지할 수 있도록 응답을 제공한다.
- 상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있다.
- 특수 문자 검증
- "카카오"가 포함된 문구 검증
### 구현 방향
- Service layer의 create와 update 로직에서 `try-catch`문을 중복하여 사용하지말고, `@ExceptionalHandler` 사용
- 상품 이름은 `@NotBlank`로 가장 엄격하게 입력값을 검증함과 동시에 `@Size`를 이용
- `@Pattern`과 `regex`를 이용
- `Custom Validator` 이용

## Step 2 : 회원 로그인
### 요구사항 분석
- 회원가입 API 구성
    - 이메일,비밀번호를 이용한 토큰 발급 기능 구현
    - (선택) 회원을 조회,추가,수정,삭제할 수 있는 관리자 화면 구성
- 로그인 API 구성
- ``401 Unauthroized`` 와 ``403 Forbidden``을 구분하여 예외처리
### 구현 방향
| 기능   | Method | URL                 |Request Body | Response Body | 상태코드      |
|------|--------|---------------------|--------------|---------------|-----------|
| 회원가입 | POST   | /api/users/register | 이메일, 비밀번호| 토큰 정보| 201:정상가입  |
| 로그인  | POST   | /api/users/login    | 이메일, 비밀번호 | 토큰 정보| 200:정상로그인 |
- ``jwt web token`` 사용
- 도메인에서 회원 인증 로직 구현(isEuqalPassword())
- 응용 서비스에서 토큰 생성 구현

## Step 3 : 위시 리스트
### 요구사항 분석
- 위시 리스트 API 구성
    - 위시 리스트에 상품 추가
    - 위시 리스트에 등록된 상품 조회
    - 위시 리스트에 담긴 상품 삭제
### 구현 방향
| 기능                | Method | URL                     | Request Information                                               | Response Body            | 상태코드   |
|-------------------|--------|-------------------------|-------------------------------------------------------------------|--------------------------|--------|
| 위시 리스트에 상품 추가     | POST   | /api/wishes             | Header: Authorization: Bearer <JWT> <br/> Body: { "productId": n } | 없음                       | 201 |
| 위시 리스트에 등록된 상품 조회 | GET    | /api/wishes             | Header: Authorization: Bearer <JWT>                               | List<ProductResponseDto> | 200    |
| 위시 리스트에 담긴 상품 삭제  | DELETE | /api/wishes/{productId} | - Header: Authorization: Bearer <JWT> <br/> PathVariable: productId   | 없음                       | 204    |
- `@LoginUser` 어노테이션 & `HandlerMethodArgumentResolver` 구현
    - 컨트롤러 메서드 파라미터에 로그인 유저 정보를 직접 주입