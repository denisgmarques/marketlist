(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .controller('MarketListDetailController', MarketListDetailController);

    MarketListDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'MarketList', 'ItemList'];

    function MarketListDetailController($scope, $rootScope, $stateParams, previousState, entity, MarketList, ItemList) {
        var vm = this;

        vm.marketList = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('marketlistApp:marketListUpdate', function(event, result) {
            vm.marketList = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
