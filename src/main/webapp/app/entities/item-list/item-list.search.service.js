(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .factory('ItemListSearch', ItemListSearch);

    ItemListSearch.$inject = ['$resource'];

    function ItemListSearch($resource) {
        var resourceUrl =  'api/_search/item-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
