(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .factory('MarketListSearch', MarketListSearch);

    MarketListSearch.$inject = ['$resource'];

    function MarketListSearch($resource) {
        var resourceUrl =  'api/_search/market-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
