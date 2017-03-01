(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .controller('ItemListDetailController', ItemListDetailController);

    ItemListDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ItemList', 'MarketList'];

    function ItemListDetailController($scope, $rootScope, $stateParams, previousState, entity, ItemList, MarketList) {
        var vm = this;

        vm.itemList = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('marketlistApp:itemListUpdate', function(event, result) {
            vm.itemList = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
