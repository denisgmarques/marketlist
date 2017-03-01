(function() {
    'use strict';
    angular
        .module('marketlistApp')
        .factory('ItemList', ItemList);

    ItemList.$inject = ['$resource'];

    function ItemList ($resource) {
        var resourceUrl =  'api/item-lists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
