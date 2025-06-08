// SPDX-License-Identifier: MIT
pragma solidity 0.8.17; // Solidity 컴파일러 버전 지정
pragma experimental ABIEncoderV2;

import "./openzeppelin-contracts-4.8.0/openzeppelin-contracts-4.8.0/contracts/token/ERC721/ERC721.sol"; // ERC721 표준 임포트
import "./openzeppelin-contracts-4.8.0/openzeppelin-contracts-4.8.0/contracts/token/ERC721/extensions/ERC721URIStorage.sol"; // URI 저장 확장 임포트
import "./openzeppelin-contracts-4.8.0/openzeppelin-contracts-4.8.0/contracts/access/Ownable.sol"; // 소유자 관리 기능 임포트 (컨트랙트 배포자에게 관리 권한 부여)

contract UsedBookMarketplace is ERC721URIStorage, Ownable {

    uint256 private _nextTokenId; // 다음으로 발행할 토큰 ID를 직접 관리하는 변수 추가

    // 판매 목록에 올라온 NFT 정보 (판매자, 가격)
    struct Listing {
        address seller; // 판매자 주소
        uint256 price;  // 판매 가격 (wei 단위)
        bool isListed;  // 판매 등록 여부
    }

    // 각 NFT 토큰 ID에 대한 판매 정보를 저장
    mapping(uint256 => Listing) public listings;

    // --- 이벤트 정의 (프론트엔드/백엔드에서 감지) ---
    event NFTMinted(uint256 indexed tokenId, address indexed owner, string tokenURI);
    event BookListed(uint256 indexed tokenId, address indexed seller, uint256 price);
    event BookPurchased(uint256 indexed tokenId, address indexed buyer, address indexed seller, uint256 price, string purchaseDate);
    event ListingRemoved(uint256 indexed tokenId);

    // 생성자: 컨트랙트 배포 시 NFT 컬렉션의 이름과 심볼 설정
    constructor()
        ERC721("UsedBookNFT", "UBN")
        Ownable() // 컨트랙트 배포자를 소유자로 설정 (관리자)
    {
        _nextTokenId = 1; // 첫 토큰 ID를 1로 시작하도록 초기화
    }

    // --- NFT 발행 (Minting) ---
    // 새로운 중고책 NFT를 발행하는 함수 (서비스 관리자 또는 특정 권한을 가진 자만 호출 가능)
    // _to: NFT를 받을 주소 (일반적으로 책을 등록하는 판매자)
    // _tokenURI: NFT 메타데이터 URI (IPFS 주소 등, 책 상세 정보 포함)
    function mintBook(address _to, string memory _tokenURI)
        public onlyOwner // 오직 컨트랙트 소유자(관리자)만 호출 가능
        returns (uint256)
    {
        uint256 tokenId = _nextTokenId; // 현재 _nextTokenId 값을 사용
        _nextTokenId++; // 다음 토큰 ID로 증가 (Solidity 0.8.0+ 에서는 오버플로우 방지 내장)

        _mint(_to, tokenId); // _to 주소에 새로운 NFT 발행
        _setTokenURI(tokenId, _tokenURI); // 해당 NFT의 URI 설정

        emit NFTMinted(tokenId, _to, _tokenURI); // NFT 발행 이벤트 발생
        return tokenId; // 발행된 토큰 ID 반환
    }

    // --- NFT 판매 등록 (Listing) ---
    // NFT 소유자가 자신의 NFT를 판매 목록에 등록
    // _tokenId: 판매할 NFT의 토큰 ID
    // _price: 판매 가격 (wei 단위)
    function listItem(uint256 _tokenId, uint256 _price) public {
        // 1. NFT 소유권 확인
        require(ownerOf(_tokenId) == msg.sender, "UsedBookMarketplace: Caller is not the owner of the NFT.");
        // 2. 판매 가격이 0 이상인지 확인
        require(_price > 0, "UsedBookMarketplace: Price must be greater than 0.");
        // 3. 이미 판매 등록된 NFT인지 확인
        require(!listings[_tokenId].isListed, "UsedBookMarketplace: NFT is already listed for sale.");
        // 4. 마켓플레이스 컨트랙트가 NFT를 전송할 권한이 있는지 확인
        // 이 승인 트랜잭션은 listItem 함수 호출 전에 별도로 클라이언트(프론트엔드/백엔드)에서 처리되어야 합니다.
        // 예를 들어, 클라이언트에서 ERC721.approve(마켓플레이스_컨트랙트_주소, _tokenId); 를 호출하게 해야 합니다.
        require(
            getApproved(_tokenId) == address(this) || isApprovedForAll(msg.sender, address(this)),
            "UsedBookMarketplace: Marketplace contract not approved to transfer NFT."
        );

        // 판매 정보 저장
        listings[_tokenId] = Listing(msg.sender, _price, true);
        emit BookListed(_tokenId, msg.sender, _price); // 판매 등록 이벤트 발생
    }

    // --- NFT 판매 등록 취소 (Unlisting) ---
    // 판매자가 판매 등록된 NFT를 취소
    // _tokenId: 판매 취소할 NFT의 토큰 ID
    function unlistItem(uint256 _tokenId) public {
        // 1. 판매자인지 확인
        require(listings[_tokenId].seller == msg.sender, "UsedBookMarketplace: Caller is not the seller of the NFT.");
        // 2. 판매 등록된 NFT인지 확인
        require(listings[_tokenId].isListed, "UsedBookMarketplace: NFT is not listed for sale.");

        // 판매 정보 초기화
        delete listings[_tokenId]; // 판매 정보 삭제
        emit ListingRemoved(_tokenId); // 판매 취소 이벤트 발생
    }

    // --- NFT 구매 (Buying) ---
    // 구매자가 NFT를 구매
    // _tokenId: 구매할 NFT의 토кен ID
    function purchaseItem(uint256 _tokenId, string memory _purchaseDate) public {
        // 1. 판매 등록된 NFT인지 확인
        require(listings[_tokenId].isListed, "UsedBookMarketplace: NFT is not listed for sale.");
        // 2. 구매자가 판매자와 동일하지 않은지 확인
        require(listings[_tokenId].seller != msg.sender, "UsedBookMarketplace: Cannot purchase your own NFT.");

        _transfer(ownerOf(_tokenId), msg.sender, _tokenId);

        emit BookPurchased(_tokenId, msg.sender, listings[_tokenId].seller, listings[_tokenId].price, _purchaseDate); // 구매 완료 이벤트 발생

        // 판매 정보 삭제 (거래 완료)
        delete listings[_tokenId];
    }

    // --- 유틸리티 함수 (NFT 정보 조회) ---
    // 특정 토큰의 판매 정보를 가져오는 함수 (외부에서 호출 가능)
    function getListing(uint256 _tokenId) public view returns (address seller, uint256 price, bool isListed) {
        Listing storage listing = listings[_tokenId];
        return (listing.seller, listing.price, listing.isListed);
    }

    // 1. 특정 토큰의 주인 주소 반환
    function getOwnerOf(uint256 _tokenId) external view returns (address) {
        return ownerOf(_tokenId);
    }

    // 2. 특정 토큰의 URI 반환
    function getTokenURI(uint256 _tokenId) external view returns (string memory) {
        return tokenURI(_tokenId);
    }

    // 3. 특정 토큰의 URI 변경 (오직 NFT의 소유자만 가능)
    function updateTokenURI(uint256 _tokenId, string memory _newURI) external {
        require(ownerOf(_tokenId) == msg.sender, "UsedBookMarketplace: Only the owner can update the token URI.");
        _setTokenURI(_tokenId, _newURI);
    }

    // 호출자가 소유한 모든 토큰들의 tokenId + URI 쌍 반환
    function getMyTokenInfos() external view returns (uint256[] memory, string[] memory) {
        uint256 total = _nextTokenId - 1;
        uint256 count = 0;

        // 먼저 사용자가 소유한 토큰 개수 계산
        for (uint256 i = 1; i <= total; i++) {
            if (ownerOf(i) == msg.sender) {
                count++;
            }
        }

        // 배열 선언
        uint256[] memory tokenIds = new uint256[](count);
        string[] memory uris = new string[](count);

        uint256 index = 0;
        for (uint256 i = 1; i <= total; i++) {
            if (ownerOf(i) == msg.sender) {
                tokenIds[index] = i;
                uris[index] = tokenURI(i);
                index++;
            }
        }

        return (tokenIds, uris);
    }

    // 현재 판매 중인 모든 토큰들의 tokenId + URI 쌍 반환
    function getAllListedTokenInfos() external view returns (uint256[] memory, string[] memory) {
        uint256 total = _nextTokenId - 1;
        uint256 count = 0;

        // 먼저 판매 중인 수 카운트
        for (uint256 i = 1; i <= total; i++) {
            if (listings[i].isListed) {
                count++;
            }
        }

        uint256[] memory tokenIds = new uint256[](count);
        string[] memory uris = new string[](count);

        uint256 index = 0;
        for (uint256 i = 1; i <= total; i++) {
            if (listings[i].isListed) {
                tokenIds[index] = i;
                uris[index] = tokenURI(i);
                index++;
            }
        }

        return (tokenIds, uris);
    }
}