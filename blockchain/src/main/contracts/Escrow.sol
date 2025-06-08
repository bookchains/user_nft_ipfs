// SPDX-License-Identifier: MIT
pragma solidity 0.8.17;

contract SimpleEscrow {
    uint256 private dealCounter = 0;

    struct Deal {
        address buyer;
        address seller;
        uint256 price;
        bool isConfirmed;
    }

    mapping(uint256 => Deal) public deals;

    event DealInitiated(uint256 indexed dealId, address indexed buyer, address indexed seller, uint256 price);
    event DealCompleted(uint256 indexed dealId);
    event DealCancelled(uint256 indexed dealId);

    function initiateDeal(address seller) external payable returns (uint256) {
        require(msg.sender != seller, "Buyer cannot be seller");
        require(msg.value > 0, "Must send ETH");

        dealCounter++;
        deals[dealCounter] = Deal({
            buyer: msg.sender,
            seller: seller,
            price: msg.value,
            isConfirmed: false
        });

        emit DealInitiated(dealCounter, msg.sender, seller, msg.value);
        return dealCounter;  // 새로 생성된 dealId 반환
    }



    modifier onlyBuyer(uint256 dealId) {
        require(msg.sender == deals[dealId].buyer, "Not buyer");
        _;
    }

    function confirmDeal(uint256 dealId) external onlyBuyer(dealId) returns (bool) {
        Deal storage deal = deals[dealId];
        require(deal.price > 0, "No deal");
        require(!deal.isConfirmed, "Already confirmed");

        deal.isConfirmed = true;

        payable(deal.seller).transfer(deal.price);

        emit DealCompleted(dealId);

        return true;
    }

    function cancelDeal(uint256 dealId) external onlyBuyer(dealId) returns (bool) {
        Deal memory deal = deals[dealId];
        require(!deal.isConfirmed, "Already confirmed");

        payable(deal.buyer).transfer(deal.price);

        emit DealCancelled(dealId);

        return true;
    }

    function isDealConfirmed(uint256 dealId) external view returns (bool) {
        return deals[dealId].isConfirmed;
    }
}
