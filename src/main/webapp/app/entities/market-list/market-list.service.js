(function() {
    'use strict';
    angular
        .module('marketlistApp')
        .factory('MarketList', MarketList);

    MarketList.$inject = ['$resource', 'DateUtils'];

    function MarketList ($resource, DateUtils) {
        var resourceUrl =  'api/market-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
