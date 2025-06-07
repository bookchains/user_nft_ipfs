// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";

contract BookNFT is ERC721URIStorage {
    uint public bookCount;

    struct Trade {
        address from;
        address to;
        uint price;
        uint timestamp;
    }

    mapping(uint => address) public currentOwner;
    mapping(uint => bool) public isForSale;
    mapping(uint => Trade[]) public tradeHistory;

    event BookMinted(uint tokenId, address owner, string tokenURI);
    event BookTransferred(uint tokenId, address from, address to, uint price);

    constructor() ERC721("BookNFT", "BNFT") {}

    function mintBook(address to, string memory tokenURI) public returns (uint) {
        bookCount += 1;
        uint tokenId = bookCount;
        _mint(to, tokenId);
        _setTokenURI(tokenId, tokenURI);
        currentOwner[tokenId] = to;
        isForSale[tokenId] = false;
        emit BookMinted(tokenId, to, tokenURI);
        return tokenId;
    }

    function transferBook(uint tokenId, address to, uint price) public {
        require(msg.sender == ownerOf(tokenId), "Not owner");
        _transfer(msg.sender, to, tokenId);
        Trade memory trade = Trade({
            from: msg.sender,
            to: to,
            price: price,
            timestamp: block.timestamp
        });
        tradeHistory[tokenId].push(trade);
        currentOwner[tokenId] = to;
        isForSale[tokenId] = false;
        emit BookTransferred(tokenId, msg.sender, to, price);
    }

    function setForSale(uint tokenId, bool sale) public {
        require(msg.sender == ownerOf(tokenId), "Not owner");
        isForSale[tokenId] = sale;
    }

    function getTradeHistory(uint tokenId) public view returns (Trade[] memory) {
        return tradeHistory[tokenId];
    }
}
