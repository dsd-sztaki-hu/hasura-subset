package hu.sztaki.dsd.hasura.subset


data class SnapshotTest(
    val description: String,
    val graphql: String,
    val jsonResult: String,
    val expectedGraphql: String,
    val expectedVariables: String
) {
}

val tests = mutableListOf<SnapshotTest>(
    SnapshotTest(
        description = "test1 - with single publication query",
        graphql = """
            query {
              publication(mtid:3156694) {
                __typename
                dtype
                otype
                mtid
                title
                deleted
                published
                unhandledTickets
                authorCount
                citation
                citationCount
                citationCountUnpublished
                citationCountWoOther
                citedCount
                citedPubCount
                citingPubCount
                contributorCount
                core
                doiCitationCount
                independentCitationCount
                independentCitCountWoOther
                independentCitingPubCount
                oaFree
                ownerAuthorCount
                ownerInstituteCount
                publicationPending
                scopusCitationCount
                unhandledCitationCount
                unhandledCitingPubCount
                wosCitationCount                
                authorships {
                  __typename
                  dtype
                  otype
                  mtid  
                  familyName
                  givenName
                  deleted
                  published
                  unhandledTickets
                  listPosition
                  oldListPosition
                  share 
                  author {
                    __typename
                    dtype
                    otype
                    mtid
                    deleted
                    published
                    unhandledTickets 
                    authorNames {
                      __typename
                      otype
                      mtid           
                      familyName
                      givenName
                      fullName
                      deleted
                      published
                      unhandledTickets 
                    }
                  }
                }
              }
            }
            """.trimIndent(),
        jsonResult = """
            {
              "data": {
                "publication": {
                  "__typename": "publication",
                  "dtype": "JournalArticle",
                  "otype": "JournalArticle",
                  "mtid": "3156694",
                  "title": "Comparison of quenched and annealed invariance principles for random conductance model",
                  "deleted": false,
                  "published": true,
                  "unhandledTickets": 0,
                  "authorCount": 3,
                  "citation": false,
                  "citationCount": 1,
                  "citationCountUnpublished": 0,
                  "citationCountWoOther": 1,
                  "citedCount": 0,
                  "citedPubCount": 0,
                  "citingPubCount": 1,
                  "contributorCount": 0,
                  "core": true,
                  "doiCitationCount": 1,
                  "independentCitationCount": 1,
                  "independentCitCountWoOther": 1,
                  "independentCitingPubCount": 1,
                  "oaFree": false,
                  "ownerAuthorCount": 1,
                  "ownerInstituteCount": 5,
                  "publicationPending": false,
                  "scopusCitationCount": 0,
                  "unhandledCitationCount": 0,
                  "unhandledCitingPubCount": 0,
                  "wosCitationCount": 1,
                  "authorships": [
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445519",
                      "familyName": "Barlow",
                      "givenName": "M",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 1,
                      "oldListPosition": 1,
                      "share": 0.333333,
                      "author": null
                    },
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445520",
                      "familyName": "Burdzy",
                      "givenName": "K",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 2,
                      "oldListPosition": 2,
                      "share": 0.333333,
                      "author": null
                    },
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445521",
                      "familyName": "Timar",
                      "givenName": "A",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 3,
                      "oldListPosition": 3,
                      "share": 0.333333,
                      "author": {
                        "__typename": "users",
                        "dtype": "Author",
                        "otype": "Author",
                        "mtid": "10030578",
                        "deleted": false,
                        "published": true,
                        "unhandledTickets": 0,
                        "authorNames": [
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145652",
                            "familyName": "Timár",
                            "givenName": "Ádám",
                            "fullName": "Timár Ádám",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145653",
                            "familyName": "Ádám",
                            "givenName": "Timár",
                            "fullName": "Ádám Timár",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145654",
                            "familyName": "Tímár",
                            "givenName": "Á",
                            "fullName": "Tímár Á",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145655",
                            "familyName": "Á",
                            "givenName": "Tímár",
                            "fullName": "Á Tímár",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145656",
                            "familyName": "Timar",
                            "givenName": "Adam",
                            "fullName": "Timar Adam",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145657",
                            "familyName": "Adam",
                            "givenName": "Timar",
                            "fullName": "Adam Timar",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          }
                        ]
                      }
                    }
                  ]
                }
              }
            }
            """.trimIndent(),
        expectedGraphql = """
mutation hasuraSubset(${"$"}objects_publication: [publication_insert_input!]!, ${"$"}on_conflict_publication: publication_on_conflict) {
publication: insert_publication(objects: ${"$"}objects_publication, on_conflict: ${"$"}on_conflict_publication) {
    affected_rows
}
}

""".trimIndent(),
        expectedVariables = """{"objects_publication":{"dtype":"JournalArticle","otype":"JournalArticle","mtid":"3156694","title":"Comparison of quenched and annealed invariance principles for random conductance model","deleted":false,"published":true,"unhandledTickets":0,"authorCount":3,"citation":false,"citationCount":1,"citationCountUnpublished":0,"citationCountWoOther":1,"citedCount":0,"citedPubCount":0,"citingPubCount":1,"contributorCount":0,"core":true,"doiCitationCount":1,"independentCitationCount":1,"independentCitCountWoOther":1,"independentCitingPubCount":1,"oaFree":false,"ownerAuthorCount":1,"ownerInstituteCount":5,"publicationPending":false,"scopusCitationCount":0,"unhandledCitationCount":0,"unhandledCitingPubCount":0,"wosCitationCount":1,"authorships":{"data":[{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445519","familyName":"Barlow","givenName":"M","deleted":false,"published":false,"unhandledTickets":0,"listPosition":1,"oldListPosition":1,"share":0.333333,"author":null},{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445520","familyName":"Burdzy","givenName":"K","deleted":false,"published":false,"unhandledTickets":0,"listPosition":2,"oldListPosition":2,"share":0.333333,"author":null},{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445521","familyName":"Timar","givenName":"A","deleted":false,"published":false,"unhandledTickets":0,"listPosition":3,"oldListPosition":3,"share":0.333333,"author":{"data":{"dtype":"Author","otype":"Author","mtid":"10030578","deleted":false,"published":true,"unhandledTickets":0,"authorNames":{"data":[{"otype":"AuthorName","mtid":"145652","familyName":"Timár","givenName":"Ádám","fullName":"Timár Ádám","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145653","familyName":"Ádám","givenName":"Timár","fullName":"Ádám Timár","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145654","familyName":"Tímár","givenName":"Á","fullName":"Tímár Á","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145655","familyName":"Á","givenName":"Tímár","fullName":"Á Tímár","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145656","familyName":"Timar","givenName":"Adam","fullName":"Timar Adam","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145657","familyName":"Adam","givenName":"Timar","fullName":"Adam Timar","deleted":false,"published":false,"unhandledTickets":0}],"on_conflict":{"constraint":"author_name_pkey","update_columns":["mtid"]}}},"on_conflict":{"constraint":"users_pkey","update_columns":["mtid"]}}}],"on_conflict":{"constraint":"authorship_pkey","update_columns":["mtid"]}}},"on_conflict_publication":{"constraint":"publication_pkey","update_columns":["mtid"]}}""".trimIndent()
    ),

    SnapshotTest(
        description = "test2 - with multiple publication queries",
        graphql = """
            {
              pub1: publication(mtid: 3156695) {
                __typename
                dtype
                otype
                mtid
                title
                deleted
                published
                unhandledTickets
                authorCount
                citation
                citationCount
                citationCountUnpublished
                citationCountWoOther
                citedCount
                citedPubCount
                citingPubCount
                contributorCount
                core
                doiCitationCount
                independentCitationCount
                independentCitCountWoOther
                independentCitingPubCount
                oaFree
                ownerAuthorCount
                ownerInstituteCount
                publicationPending
                scopusCitationCount
                unhandledCitationCount
                unhandledCitingPubCount
                wosCitationCount
                authorships {
                  __typename
                  dtype
                  otype
                  mtid
                  familyName
                  givenName
                  deleted
                  published
                  unhandledTickets
                  listPosition
                  oldListPosition
                  share
                  author {
                    __typename
                    dtype
                    otype
                    mtid
                    deleted
                    published
                    unhandledTickets
                    authorNames {
                      __typename
                      otype
                      mtid
                      familyName
                      givenName
                      fullName
                      deleted
                      published
                      unhandledTickets
                    }
                  }
                }
              }  
              pub2: publication(mtid: 3156694) {
                __typename
                dtype
                otype
                mtid
                title
                deleted
                published
                unhandledTickets
                authorCount
                citation
                citationCount
                citationCountUnpublished
                citationCountWoOther
                citedCount
                citedPubCount
                citingPubCount
                contributorCount
                core
                doiCitationCount
                independentCitationCount
                independentCitCountWoOther
                independentCitingPubCount
                oaFree
                ownerAuthorCount
                ownerInstituteCount
                publicationPending
                scopusCitationCount
                unhandledCitationCount
                unhandledCitingPubCount
                wosCitationCount
                authorships {
                  __typename
                  dtype
                  otype
                  mtid
                  familyName
                  givenName
                  deleted
                  published
                  unhandledTickets
                  listPosition
                  oldListPosition
                  share
                  author {
                    __typename
                    dtype
                    otype
                    mtid
                    deleted
                    published
                    unhandledTickets
                    authorNames {
                      __typename
                      otype
                      mtid
                      familyName
                      givenName
                      fullName
                      deleted
                      published
                      unhandledTickets
                    }
                  }
                }
              }
            }

            """.trimIndent(),
        jsonResult = """
            {
              "data": {
                "pub1": {
                  "__typename": "publication",
                  "dtype": "JournalArticle",
                  "otype": "JournalArticle",
                  "mtid": "3156695",
                  "title": "A POISSON ALLOCATION OF OPTIMAL TAIL",
                  "deleted": false,
                  "published": true,
                  "unhandledTickets": 0,
                  "authorCount": 2,
                  "citation": false,
                  "citationCount": 0,
                  "citationCountUnpublished": 0,
                  "citationCountWoOther": 0,
                  "citedCount": 0,
                  "citedPubCount": 0,
                  "citingPubCount": 0,
                  "contributorCount": 0,
                  "core": true,
                  "doiCitationCount": 0,
                  "independentCitationCount": 0,
                  "independentCitCountWoOther": 0,
                  "independentCitingPubCount": 0,
                  "oaFree": true,
                  "ownerAuthorCount": 1,
                  "ownerInstituteCount": 5,
                  "publicationPending": false,
                  "scopusCitationCount": 0,
                  "unhandledCitationCount": 0,
                  "unhandledCitingPubCount": 0,
                  "wosCitationCount": 0,
                  "authorships": [
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445522",
                      "familyName": "Marko",
                      "givenName": "R",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 1,
                      "oldListPosition": 1,
                      "share": 0.5,
                      "author": null
                    },
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445523",
                      "familyName": "Timar",
                      "givenName": "A",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 2,
                      "oldListPosition": 2,
                      "share": 0.5,
                      "author": {
                        "__typename": "users",
                        "dtype": "Author",
                        "otype": "Author",
                        "mtid": "10030578",
                        "deleted": false,
                        "published": true,
                        "unhandledTickets": 0,
                        "authorNames": [
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145652",
                            "familyName": "Timár",
                            "givenName": "Ádám",
                            "fullName": "Timár Ádám",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145653",
                            "familyName": "Ádám",
                            "givenName": "Timár",
                            "fullName": "Ádám Timár",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145654",
                            "familyName": "Tímár",
                            "givenName": "Á",
                            "fullName": "Tímár Á",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145655",
                            "familyName": "Á",
                            "givenName": "Tímár",
                            "fullName": "Á Tímár",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145656",
                            "familyName": "Timar",
                            "givenName": "Adam",
                            "fullName": "Timar Adam",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145657",
                            "familyName": "Adam",
                            "givenName": "Timar",
                            "fullName": "Adam Timar",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          }
                        ]
                      }
                    }
                  ]
                },
                "pub2": {
                  "__typename": "publication",
                  "dtype": "JournalArticle",
                  "otype": "JournalArticle",
                  "mtid": "3156694",
                  "title": "Comparison of quenched and annealed invariance principles for random conductance model",
                  "deleted": false,
                  "published": true,
                  "unhandledTickets": 0,
                  "authorCount": 3,
                  "citation": false,
                  "citationCount": 1,
                  "citationCountUnpublished": 0,
                  "citationCountWoOther": 1,
                  "citedCount": 0,
                  "citedPubCount": 0,
                  "citingPubCount": 1,
                  "contributorCount": 0,
                  "core": true,
                  "doiCitationCount": 1,
                  "independentCitationCount": 1,
                  "independentCitCountWoOther": 1,
                  "independentCitingPubCount": 1,
                  "oaFree": false,
                  "ownerAuthorCount": 1,
                  "ownerInstituteCount": 5,
                  "publicationPending": false,
                  "scopusCitationCount": 0,
                  "unhandledCitationCount": 0,
                  "unhandledCitingPubCount": 0,
                  "wosCitationCount": 1,
                  "authorships": [
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445519",
                      "familyName": "Barlow",
                      "givenName": "M",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 1,
                      "oldListPosition": 1,
                      "share": 0.333333,
                      "author": null
                    },
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445520",
                      "familyName": "Burdzy",
                      "givenName": "K",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 2,
                      "oldListPosition": 2,
                      "share": 0.333333,
                      "author": null
                    },
                    {
                      "__typename": "authorship",
                      "dtype": "PersonAuthorship",
                      "otype": "PersonAuthorship",
                      "mtid": "4445521",
                      "familyName": "Timar",
                      "givenName": "A",
                      "deleted": false,
                      "published": false,
                      "unhandledTickets": 0,
                      "listPosition": 3,
                      "oldListPosition": 3,
                      "share": 0.333333,
                      "author": {
                        "__typename": "users",
                        "dtype": "Author",
                        "otype": "Author",
                        "mtid": "10030578",
                        "deleted": false,
                        "published": true,
                        "unhandledTickets": 0,
                        "authorNames": [
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145652",
                            "familyName": "Timár",
                            "givenName": "Ádám",
                            "fullName": "Timár Ádám",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145653",
                            "familyName": "Ádám",
                            "givenName": "Timár",
                            "fullName": "Ádám Timár",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145654",
                            "familyName": "Tímár",
                            "givenName": "Á",
                            "fullName": "Tímár Á",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145655",
                            "familyName": "Á",
                            "givenName": "Tímár",
                            "fullName": "Á Tímár",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145656",
                            "familyName": "Timar",
                            "givenName": "Adam",
                            "fullName": "Timar Adam",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          },
                          {
                            "__typename": "author_name",
                            "otype": "AuthorName",
                            "mtid": "145657",
                            "familyName": "Adam",
                            "givenName": "Timar",
                            "fullName": "Adam Timar",
                            "deleted": false,
                            "published": false,
                            "unhandledTickets": 0
                          }
                        ]
                      }
                    }
                  ]
                }
              }
            }
            """.trimIndent(),
        expectedGraphql = """
mutation hasuraSubset(${"$"}objects_pub1: [publication_insert_input!]!, ${"$"}on_conflict_pub1: publication_on_conflict${"$"}objects_pub2: [publication_insert_input!]!, ${"$"}on_conflict_pub2: publication_on_conflict) {
pub1: insert_publication(objects: ${"$"}objects_pub1, on_conflict: ${"$"}on_conflict_pub1) {
    affected_rows
}
pub2: insert_publication(objects: ${"$"}objects_pub2, on_conflict: ${"$"}on_conflict_pub2) {
    affected_rows
}
}

""".trimIndent(),
        expectedVariables = """{"objects_pub1":{"dtype":"JournalArticle","otype":"JournalArticle","mtid":"3156695","title":"A POISSON ALLOCATION OF OPTIMAL TAIL","deleted":false,"published":true,"unhandledTickets":0,"authorCount":2,"citation":false,"citationCount":0,"citationCountUnpublished":0,"citationCountWoOther":0,"citedCount":0,"citedPubCount":0,"citingPubCount":0,"contributorCount":0,"core":true,"doiCitationCount":0,"independentCitationCount":0,"independentCitCountWoOther":0,"independentCitingPubCount":0,"oaFree":true,"ownerAuthorCount":1,"ownerInstituteCount":5,"publicationPending":false,"scopusCitationCount":0,"unhandledCitationCount":0,"unhandledCitingPubCount":0,"wosCitationCount":0,"authorships":{"data":[{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445522","familyName":"Marko","givenName":"R","deleted":false,"published":false,"unhandledTickets":0,"listPosition":1,"oldListPosition":1,"share":0.5,"author":null},{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445523","familyName":"Timar","givenName":"A","deleted":false,"published":false,"unhandledTickets":0,"listPosition":2,"oldListPosition":2,"share":0.5,"author":{"data":{"dtype":"Author","otype":"Author","mtid":"10030578","deleted":false,"published":true,"unhandledTickets":0,"authorNames":{"data":[{"otype":"AuthorName","mtid":"145652","familyName":"Timár","givenName":"Ádám","fullName":"Timár Ádám","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145653","familyName":"Ádám","givenName":"Timár","fullName":"Ádám Timár","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145654","familyName":"Tímár","givenName":"Á","fullName":"Tímár Á","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145655","familyName":"Á","givenName":"Tímár","fullName":"Á Tímár","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145656","familyName":"Timar","givenName":"Adam","fullName":"Timar Adam","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145657","familyName":"Adam","givenName":"Timar","fullName":"Adam Timar","deleted":false,"published":false,"unhandledTickets":0}],"on_conflict":{"constraint":"author_name_pkey","update_columns":["mtid"]}}},"on_conflict":{"constraint":"users_pkey","update_columns":["mtid"]}}}],"on_conflict":{"constraint":"authorship_pkey","update_columns":["mtid"]}}},"on_conflict_pub1":{"constraint":"publication_pkey","update_columns":["mtid"]},"objects_pub2":{"dtype":"JournalArticle","otype":"JournalArticle","mtid":"3156694","title":"Comparison of quenched and annealed invariance principles for random conductance model","deleted":false,"published":true,"unhandledTickets":0,"authorCount":3,"citation":false,"citationCount":1,"citationCountUnpublished":0,"citationCountWoOther":1,"citedCount":0,"citedPubCount":0,"citingPubCount":1,"contributorCount":0,"core":true,"doiCitationCount":1,"independentCitationCount":1,"independentCitCountWoOther":1,"independentCitingPubCount":1,"oaFree":false,"ownerAuthorCount":1,"ownerInstituteCount":5,"publicationPending":false,"scopusCitationCount":0,"unhandledCitationCount":0,"unhandledCitingPubCount":0,"wosCitationCount":1,"authorships":{"data":[{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445519","familyName":"Barlow","givenName":"M","deleted":false,"published":false,"unhandledTickets":0,"listPosition":1,"oldListPosition":1,"share":0.333333,"author":null},{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445520","familyName":"Burdzy","givenName":"K","deleted":false,"published":false,"unhandledTickets":0,"listPosition":2,"oldListPosition":2,"share":0.333333,"author":null},{"dtype":"PersonAuthorship","otype":"PersonAuthorship","mtid":"4445521","familyName":"Timar","givenName":"A","deleted":false,"published":false,"unhandledTickets":0,"listPosition":3,"oldListPosition":3,"share":0.333333,"author":{"data":{"dtype":"Author","otype":"Author","mtid":"10030578","deleted":false,"published":true,"unhandledTickets":0,"authorNames":{"data":[{"otype":"AuthorName","mtid":"145652","familyName":"Timár","givenName":"Ádám","fullName":"Timár Ádám","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145653","familyName":"Ádám","givenName":"Timár","fullName":"Ádám Timár","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145654","familyName":"Tímár","givenName":"Á","fullName":"Tímár Á","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145655","familyName":"Á","givenName":"Tímár","fullName":"Á Tímár","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145656","familyName":"Timar","givenName":"Adam","fullName":"Timar Adam","deleted":false,"published":false,"unhandledTickets":0},{"otype":"AuthorName","mtid":"145657","familyName":"Adam","givenName":"Timar","fullName":"Adam Timar","deleted":false,"published":false,"unhandledTickets":0}],"on_conflict":{"constraint":"author_name_pkey","update_columns":["mtid"]}}},"on_conflict":{"constraint":"users_pkey","update_columns":["mtid"]}}}],"on_conflict":{"constraint":"authorship_pkey","update_columns":["mtid"]}}},"on_conflict_pub2":{"constraint":"publication_pkey","update_columns":["mtid"]}}""".trimIndent()
    )

)


val graphqlQueryExample = """
            query exampleQuery(${"$"}param: [bigint!]!){
              publication(mtid:${"$"}param) {
                __typename
                dtype
                otype
                mtid
                title
                deleted
                published
                unhandledTickets
                authorCount
                citation
                citationCount
                citationCountUnpublished
                citationCountWoOther
                citedCount
                citedPubCount
                citingPubCount
                contributorCount
                core
                doiCitationCount
                independentCitationCount
                independentCitCountWoOther
                independentCitingPubCount
                oaFree
                ownerAuthorCount
                ownerInstituteCount
                publicationPending
                scopusCitationCount
                unhandledCitationCount
                unhandledCitingPubCount
                wosCitationCount                
                authorships {
                  __typename
                  dtype
                  otype
                  mtid  
                  familyName
                  givenName
                  deleted
                  published
                  unhandledTickets
                  listPosition
                  oldListPosition
                  share 
                  author {
                    __typename
                    dtype
                    otype
                    mtid
                    deleted
                    published
                    unhandledTickets 
                    authorNames {
                      __typename
                      otype
                      mtid           
                      familyName
                      givenName
                      fullName
                      deleted
                      published
                      unhandledTickets 
                    }
                  }
                }
              }
            }
            """.trimIndent()


private val graphqlSchemaExample = """
            "A ToDo Object"
            type Todo {
                "A unique identifier"
                id: String!
                name: String!
                completed: Boolean
                color: Color
                "A required list containing colors that cannot contain nulls"
                requiredColors: [Color!]!
                "A non-required list containing colors that cannot contain nulls"
                optionalColors: [Color!]
                fieldWithOptionalArgument(
                  optionalFilter: [String!]
                ): [String!]
                fieldWithRequiredArgument(
                  requiredFilter: [String!]!
                ): [String!]
            }
            ""${'"'}
            A type that describes ToDoInputType. Its description might not
            fit within the bounds of 80 width and so you want MULTILINE
            ""${'"'}
            input TodoInputType {
                name: String!
                completed: Boolean
                color: Color=RED
            }
            enum Color {
              "Red color"
              RED
              "Green color"
              GREEN
            }
            type Query {
                todo(
                    "todo identifier"
                    id: String!
                    isCompleted: Boolean=false
                    requiredStatuses: [String!]!
                    optionalStatuses: [String!]
                ): Todo!
                todos: [Todo!]!
            }
            type Mutation {
                update_todo(id: String!, todo: TodoInputType!): Todo
                create_todo(todo: TodoInputType!): Todo
            }
        """.trimIndent()

val graphqlQueryExample1 = """
    query exampleQuery(${"$"}param: [bigint]!){
      publication(mtid:${"$"}param) {
        __typename
        id
      }
    }
""".trimIndent()

val graphqlQueryExample2 = """
            query exampleQuery(${"$"}param: [bigint!]!){
              publication(mtid:${"$"}param) {
                __typename
                dtype
                otype
                mtid
                title
                deleted
                published
                unhandledTickets
                authorCount
                citation
                citationCount
                citationCountUnpublished
                citationCountWoOther
                citedCount
                citedPubCount
                citingPubCount
                contributorCount
                core
                doiCitationCount
                independentCitationCount
                independentCitCountWoOther
                independentCitingPubCount
                oaFree
                ownerAuthorCount
                ownerInstituteCount
                publicationPending
                scopusCitationCount
                unhandledCitationCount
                unhandledCitingPubCount
                wosCitationCount                
                authorships {
                  __typename
                  dtype
                  otype
                  mtid  
                  familyName
                  givenName
                  deleted
                  published
                  unhandledTickets
                  listPosition
                  oldListPosition
                  share 
                  author {
                    __typename
                    dtype
                    otype
                    mtid
                    deleted
                    published
                    unhandledTickets 
                    authorNames {
                      __typename
                      otype
                      mtid           
                      familyName
                      givenName
                      fullName
                      deleted
                      published
                      unhandledTickets 
                    }
                  }
                }
              }
              
                author {
                  __typename
                  dtype
                  otype
                  mtid
                  deleted
                  published
                  unhandledTickets 
                  authorNames {
                    __typename
                    otype
                    mtid           
                    familyName
                    givenName
                    fullName
                    deleted
                    published
                    unhandledTickets 
                  }
                }
            }
            """.trimIndent()

val graphqlQueryExampleMtmt = """
            query exampleQuery(${"$"}param: bigint!){
              publication(mtid:${"$"}param) {
                __everything            
                authorships {
                  __everything            
                  author {
                    __everything            
                    authorNames {
                      __everything            
                    }
                  }
                }
              }
            }
            """.trimIndent()

val graphqlQueryExampleMtmtExpanded = """
query exampleQuery(${"$"}param: bigint!)  {
  publication(mtid: ${"$"}param) {
    abstractText
    acceptanceYear
    adminApproved
    adminApproverForSort
    adminApproverMtid
    altTitles
    applicationYear
    approved
    approverMtid
    authorCount
    bookMtid
    bulkDuplumSearchDone
    caseNumber
    categoryForSort
    categoryMtid
    chapterCount
    checked
    checkerMtid
    citation
    citationCount
    citationCountUnpublished
    citationCountWoOther
    citedCount
    citedPubCount
    citingPubCount
    citingPubCountWoOther
    collaboration
    comment
    comment2
    conferenceMtid
    conferencePublication
    consultant
    consultant2
    consultantAuthor2Mtid
    consultantAuthorMtid
    contributorCount
    core
    countryMtid
    created
    creator
    deleted
    deletedDate
    description0
    description1
    description2
    description3
    description4
    description5
    digital
    directInstituteCount
    directInstitutesForSort
    disciplineMtid
    doiCitationCount
    dtype
    duplumKey
    duplumRole
    duplumSearchResultMtid
    editionNumber
    endDate
    error
    externalSource
    firstAuthor
    firstPage
    firstPageOrInternalIdForSort
    foreignEdition
    foreignEditionCitationCount
    foreignLanguage
    fromCitation
    fullPublication
    group_mtid
    hasCitationDuplums
    ifRatingMtid
    impactFactor
    inSelectedPubs
    independentCitCountWoOther
    independentCitationCount
    independentCitingPubCount
    independentCitingPubCountWoOther
    internalId
    ipc
    issue
    journalForSort
    journalMtid
    journalName
    labelEng
    labelHun
    languagesForSort
    lastDuplumOK
    lastDuplumSearch
    lastModified
    lastModifier
    lastModifierAdmin
    lastPage
    lastRefresh
    lastTemplateMake
    lastTouched
    locked
    lockerMtid
    mabDisciplineMtid
    missingAuthor
    mtid
    nationalOrigin
    nationalOriginCitationCount
    number
    oaByAuthorMtid
    oaCheckDate
    oaEmbargoDate
    oaFree
    oaLink
    oaType
    oaTypeDisp
    oldId
    oldTimestamp
    otype
    ownerAuthorCount
    ownerInstituteCount
    packet
    pageLength
    patentCountryMtid
    prevValid
    printed
    pubStats
    publicationPending
    publishDate
    published
    publishedYear
    publishedYearEnd
    ratingsForSort
    referenceList
    refreshed
    reviewer
    school
    scopusCitationCount
    selfCitationCount
    sourceOfData
    sourceYear
    startDate
    status
    subSubTypeMtid
    subTitle
    subTypeForSort
    subTypeMtid
    submissionNumber
    submissionYear
    tempLocked
    tempLockerIdString
    template2Eng
    template2Hun
    templateEng
    templateHun
    title
    typeForSort
    typeMtid
    unhandledCitationCount
    unhandledCitingPubCount
    unhandledTickets
    unprocessedData
    userChangeableUntil
    validFromYear
    validToYear
    validated
    validatorForSort
    validatorMtid
    volume
    volumeNumber
    volumeTitle
    wosCitationCount
    authorships {
      affiliation
      approved
      approverMtid
      authorMtid
      authorTyped
      comment
      comment2
      corresponding
      created
      creator
      deleted
      deletedDate
      dtype
      editorTyped
      error
      familyName
      first
      fullName
      givenName
      labelEng
      labelHun
      last
      lastModified
      lastModifier
      lastModifierAdmin
      lastRefresh
      lastTemplateMake
      lastTouched
      listPosition
      locked
      lockerMtid
      mtid
      oldId
      oldListPosition
      oldTimestamp
      orcid
      otherTyped
      otype
      prevValid
      publicationMtid
      published
      refreshed
      share
      status
      template2Eng
      template2Hun
      templateEng
      templateHun
      typeMtid
      unhandledTickets
      validFromYear
      validToYear
      author {
        affiliationsForSort
        allowedIps
        altTab
        approved
        approverMtid
        auxName
        birthDate
        birthPlace
        chosenUserName
        citationCount
        citingPubCount
        citsCompleteEnd
        citsCompleteStart
        comment
        comment2
        created
        creator
        dead
        deathDate
        deleted
        deletedDate
        digestMode
        doiCitationCount
        dtype
        duplumRole
        duplumSearchResultMtid
        email
        emailAddressConfirmed
        enabled
        error
        familyName
        foreignEditionCitationCount
        gender
        givenName
        inactivatedAt
        inactivatedByMtid
        inactivationComment
        inactiveFrom
        independentCitationCount
        independentCitingPubCount
        labelEng
        labelHun
        lastDataChange
        lastDuplumOK
        lastDuplumSearch
        lastLogin
        lastLogin2
        lastModified
        lastModifier
        lastModifierAdmin
        lastOnlineAction
        lastRefresh
        lastTemplateMake
        lastTouched
        locked
        lockerMtid
        moreCitationsPerCitingDoc
        mtid
        nationalOriginCitationCount
        needsToChangePasswordUntil
        oldId
        oldTimestamp
        otype
        passwordHash
        phone
        policyAcceptDate
        prevValid
        pubStats
        publicationCount
        published
        pubsCompleteEnd
        pubsCompleteStart
        pwFormat
        receiveEmailAlertsForForumEvents
        receiveEmailAlertsForTicketEvents
        refreshed
        registrationComment
        registrationDate
        rights
        robot
        robotSupervisorMtid
        scopusCitationCount
        selectedPubListIsEmpty
        selectedPubListIsOpen
        selectedPubListMtid
        shibCreated
        shibId
        shibIdProvider
        speciality
        status
        summaryTable2Code
        summaryTable2Mtid
        summaryTable2csvMtid
        summaryTable2engMtid
        summaryTable2templateMtid
        summaryTableCsvMtid
        summaryTableEngMtid
        summaryTableMtid
        template2Eng
        template2Hun
        templateEng
        templateHun
        temporaryAccessToken
        temporaryAccessTokenValidBefore
        unhandledCitationCount
        unhandledCitingPubCount
        unhandledTickets
        userNotificationTimeMtid
        username
        validFromYear
        validToYear
        wosCitationCount
        authorNames {
          approved
          approverMtid
          authorMtid
          comment
          comment2
          created
          creator
          deleted
          deletedDate
          error
          familyName
          fullName
          givenName
          labelEng
          labelHun
          lastModified
          lastModifier
          lastModifierAdmin
          lastRefresh
          lastTemplateMake
          lastTouched
          locked
          lockerMtid
          mtid
          oldId
          oldTimestamp
          otype
          prevValid
          published
          refreshed
          status
          template2Eng
          template2Hun
          templateEng
          templateHun
          unhandledTickets
          validFromYear
          validToYear
        }
      }
    }
  }
}

""".trimIndent()

val graphqlQueryExampleMtmtExpandedWithUUTypename = """
query exampleQuery(${"$"}param: bigint!)  {
  publication(mtid: ${"$"}param) {
    __typename
    abstractText
    acceptanceYear
    adminApproved
    adminApproverForSort
    adminApproverMtid
    altTitles
    applicationYear
    approved
    approverMtid
    authorCount
    bookMtid
    bulkDuplumSearchDone
    caseNumber
    categoryForSort
    categoryMtid
    chapterCount
    checked
    checkerMtid
    citation
    citationCount
    citationCountUnpublished
    citationCountWoOther
    citedCount
    citedPubCount
    citingPubCount
    citingPubCountWoOther
    collaboration
    comment
    comment2
    conferenceMtid
    conferencePublication
    consultant
    consultant2
    consultantAuthor2Mtid
    consultantAuthorMtid
    contributorCount
    core
    countryMtid
    created
    creator
    deleted
    deletedDate
    description0
    description1
    description2
    description3
    description4
    description5
    digital
    directInstituteCount
    directInstitutesForSort
    disciplineMtid
    doiCitationCount
    dtype
    duplumKey
    duplumRole
    duplumSearchResultMtid
    editionNumber
    endDate
    error
    externalSource
    firstAuthor
    firstPage
    firstPageOrInternalIdForSort
    foreignEdition
    foreignEditionCitationCount
    foreignLanguage
    fromCitation
    fullPublication
    group_mtid
    hasCitationDuplums
    ifRatingMtid
    impactFactor
    inSelectedPubs
    independentCitCountWoOther
    independentCitationCount
    independentCitingPubCount
    independentCitingPubCountWoOther
    internalId
    ipc
    issue
    journalForSort
    journalMtid
    journalName
    labelEng
    labelHun
    languagesForSort
    lastDuplumOK
    lastDuplumSearch
    lastModified
    lastModifier
    lastModifierAdmin
    lastPage
    lastRefresh
    lastTemplateMake
    lastTouched
    locked
    lockerMtid
    mabDisciplineMtid
    missingAuthor
    mtid
    nationalOrigin
    nationalOriginCitationCount
    number
    oaByAuthorMtid
    oaCheckDate
    oaEmbargoDate
    oaFree
    oaLink
    oaType
    oaTypeDisp
    oldId
    oldTimestamp
    otype
    ownerAuthorCount
    ownerInstituteCount
    packet
    pageLength
    patentCountryMtid
    prevValid
    printed
    pubStats
    publicationPending
    publishDate
    published
    publishedYear
    publishedYearEnd
    ratingsForSort
    referenceList
    refreshed
    reviewer
    school
    scopusCitationCount
    selfCitationCount
    sourceOfData
    sourceYear
    startDate
    status
    subSubTypeMtid
    subTitle
    subTypeForSort
    subTypeMtid
    submissionNumber
    submissionYear
    tempLocked
    tempLockerIdString
    template2Eng
    template2Hun
    templateEng
    templateHun
    title
    typeForSort
    typeMtid
    unhandledCitationCount
    unhandledCitingPubCount
    unhandledTickets
    unprocessedData
    userChangeableUntil
    validFromYear
    validToYear
    validated
    validatorForSort
    validatorMtid
    volume
    volumeNumber
    volumeTitle
    wosCitationCount
    authorships {
      __typename
      affiliation
      approved
      approverMtid
      authorMtid
      authorTyped
      comment
      comment2
      corresponding
      created
      creator
      deleted
      deletedDate
      dtype
      editorTyped
      error
      familyName
      first
      fullName
      givenName
      labelEng
      labelHun
      last
      lastModified
      lastModifier
      lastModifierAdmin
      lastRefresh
      lastTemplateMake
      lastTouched
      listPosition
      locked
      lockerMtid
      mtid
      oldId
      oldListPosition
      oldTimestamp
      orcid
      otherTyped
      otype
      prevValid
      publicationMtid
      published
      refreshed
      share
      status
      template2Eng
      template2Hun
      templateEng
      templateHun
      typeMtid
      unhandledTickets
      validFromYear
      validToYear
      author {
        __typename
        affiliationsForSort
        allowedIps
        altTab
        approved
        approverMtid
        auxName
        birthDate
        birthPlace
        chosenUserName
        citationCount
        citingPubCount
        citsCompleteEnd
        citsCompleteStart
        comment
        comment2
        created
        creator
        dead
        deathDate
        deleted
        deletedDate
        digestMode
        doiCitationCount
        dtype
        duplumRole
        duplumSearchResultMtid
        email
        emailAddressConfirmed
        enabled
        error
        familyName
        foreignEditionCitationCount
        gender
        givenName
        inactivatedAt
        inactivatedByMtid
        inactivationComment
        inactiveFrom
        independentCitationCount
        independentCitingPubCount
        labelEng
        labelHun
        lastDataChange
        lastDuplumOK
        lastDuplumSearch
        lastLogin
        lastLogin2
        lastModified
        lastModifier
        lastModifierAdmin
        lastOnlineAction
        lastRefresh
        lastTemplateMake
        lastTouched
        locked
        lockerMtid
        moreCitationsPerCitingDoc
        mtid
        nationalOriginCitationCount
        needsToChangePasswordUntil
        oldId
        oldTimestamp
        otype
        passwordHash
        phone
        policyAcceptDate
        prevValid
        pubStats
        publicationCount
        published
        pubsCompleteEnd
        pubsCompleteStart
        pwFormat
        receiveEmailAlertsForForumEvents
        receiveEmailAlertsForTicketEvents
        refreshed
        registrationComment
        registrationDate
        rights
        robot
        robotSupervisorMtid
        scopusCitationCount
        selectedPubListIsEmpty
        selectedPubListIsOpen
        selectedPubListMtid
        shibCreated
        shibId
        shibIdProvider
        speciality
        status
        summaryTable2Code
        summaryTable2Mtid
        summaryTable2csvMtid
        summaryTable2engMtid
        summaryTable2templateMtid
        summaryTableCsvMtid
        summaryTableEngMtid
        summaryTableMtid
        template2Eng
        template2Hun
        templateEng
        templateHun
        temporaryAccessToken
        temporaryAccessTokenValidBefore
        unhandledCitationCount
        unhandledCitingPubCount
        unhandledTickets
        userNotificationTimeMtid
        username
        validFromYear
        validToYear
        wosCitationCount
        authorNames {
          __typename
          approved
          approverMtid
          authorMtid
          comment
          comment2
          created
          creator
          deleted
          deletedDate
          error
          familyName
          fullName
          givenName
          labelEng
          labelHun
          lastModified
          lastModifier
          lastModifierAdmin
          lastRefresh
          lastTemplateMake
          lastTouched
          locked
          lockerMtid
          mtid
          oldId
          oldTimestamp
          otype
          prevValid
          published
          refreshed
          status
          template2Eng
          template2Hun
          templateEng
          templateHun
          unhandledTickets
          validFromYear
          validToYear
        }
      }
    }
  }
}

""".trimIndent()

val graphqlQueryExampleMtmtExpandedWithUUTypenameFiltered = """
query exampleQuery(${"$"}param: bigint!)  {
  publication(mtid: ${"$"}param) {
    __typename
    abstractText
    acceptanceYear
    adminApproved
    adminApproverForSort
    adminApproverMtid
    altTitles
    applicationYear
    approved
    approverMtid
    authorCount
    bookMtid
    bulkDuplumSearchDone
    caseNumber
    categoryForSort
    categoryMtid
    chapterCount
    checked
    checkerMtid
    citation
    citationCount
    citationCountUnpublished
    citationCountWoOther
    citedCount
    citedPubCount
    citingPubCount
    citingPubCountWoOther
    collaboration
    comment
    comment2
    conferenceMtid
    conferencePublication
    consultant
    consultant2
    consultantAuthor2Mtid
    consultantAuthorMtid
    contributorCount
    core
    countryMtid
    created
    creator
    deleted
    deletedDate
    description0
    description1
    description2
    description3
    description4
    description5
    digital
    directInstituteCount
    directInstitutesForSort
    disciplineMtid
    doiCitationCount
    dtype
    duplumKey
    duplumRole
    duplumSearchResultMtid
    editionNumber
    endDate
    error
    externalSource
    firstAuthor
    firstPage
    firstPageOrInternalIdForSort
    foreignEdition
    foreignEditionCitationCount
    foreignLanguage
    fromCitation
    fullPublication
    group_mtid
    hasCitationDuplums
    ifRatingMtid
    impactFactor
    inSelectedPubs
    independentCitCountWoOther
    independentCitationCount
    independentCitingPubCount
    independentCitingPubCountWoOther
    internalId
    ipc
    issue
    journalForSort
    journalMtid
    journalName
    labelEng
    labelHun
    languagesForSort
    lastDuplumOK
    lastDuplumSearch
    lastModified
    lastModifier
    lastModifierAdmin
    lastPage
    lastRefresh
    lastTemplateMake
    lastTouched
    locked
    lockerMtid
    mabDisciplineMtid
    missingAuthor
    mtid
    nationalOrigin
    nationalOriginCitationCount
    number
    oaByAuthorMtid
    oaCheckDate
    oaEmbargoDate
    oaFree
    oaLink
    oaType
    oaTypeDisp
    oldId
    oldTimestamp
    otype
    ownerAuthorCount
    ownerInstituteCount
    packet
    pageLength
    patentCountryMtid
    prevValid
    printed
    pubStats
    publicationPending
    publishDate
    published
    publishedYear
    publishedYearEnd
    ratingsForSort
    referenceList
    refreshed
    reviewer
    school
    scopusCitationCount
    selfCitationCount
    sourceOfData
    sourceYear
    startDate
    status
    subSubTypeMtid
    subTitle
    subTypeForSort
    subTypeMtid
    submissionNumber
    submissionYear
    tempLocked
    tempLockerIdString
    template2Eng
    template2Hun
    templateEng
    templateHun
    title
    typeForSort
    typeMtid
    unhandledCitationCount
    unhandledCitingPubCount
    unhandledTickets
    unprocessedData
    userChangeableUntil
    validFromYear
    validToYear
    validated
    validatorForSort
    validatorMtid
    volume
    volumeNumber
    volumeTitle
    wosCitationCount
    authorships {
      __typename
      affiliation
      approved
      approverMtid
      authorMtid
      authorTyped
      comment
      comment2
      corresponding
      created
      creator
      deleted
      deletedDate
      dtype
      editorTyped
      error
      familyName
      first
      fullName
      givenName
      labelEng
      labelHun
      last
      lastModified
      lastModifier
      lastModifierAdmin
      lastRefresh
      lastTemplateMake
      lastTouched
      listPosition
      locked
      lockerMtid
      mtid
      oldId
      oldListPosition
      oldTimestamp
      orcid
      otherTyped
      otype
      prevValid
      published
      refreshed
      share
      status
      template2Eng
      template2Hun
      templateEng
      templateHun
      typeMtid
      unhandledTickets
      validFromYear
      validToYear
      author {
        __typename
        affiliationsForSort
        allowedIps
        altTab
        approved
        approverMtid
        auxName
        birthDate
        birthPlace
        chosenUserName
        citationCount
        citingPubCount
        citsCompleteEnd
        citsCompleteStart
        comment
        comment2
        created
        creator
        dead
        deathDate
        deleted
        deletedDate
        digestMode
        doiCitationCount
        dtype
        duplumRole
        duplumSearchResultMtid
        email
        emailAddressConfirmed
        enabled
        error
        familyName
        foreignEditionCitationCount
        gender
        givenName
        inactivatedAt
        inactivatedByMtid
        inactivationComment
        inactiveFrom
        independentCitationCount
        independentCitingPubCount
        labelEng
        labelHun
        lastDataChange
        lastDuplumOK
        lastDuplumSearch
        lastLogin
        lastLogin2
        lastModified
        lastModifier
        lastModifierAdmin
        lastOnlineAction
        lastRefresh
        lastTemplateMake
        lastTouched
        locked
        lockerMtid
        moreCitationsPerCitingDoc
        mtid
        nationalOriginCitationCount
        needsToChangePasswordUntil
        oldId
        oldTimestamp
        otype
        passwordHash
        phone
        policyAcceptDate
        prevValid
        pubStats
        publicationCount
        published
        pubsCompleteEnd
        pubsCompleteStart
        pwFormat
        receiveEmailAlertsForForumEvents
        receiveEmailAlertsForTicketEvents
        refreshed
        registrationComment
        registrationDate
        rights
        robot
        robotSupervisorMtid
        scopusCitationCount
        selectedPubListIsEmpty
        selectedPubListIsOpen
        selectedPubListMtid
        shibCreated
        shibId
        shibIdProvider
        speciality
        status
        summaryTable2Code
        summaryTable2Mtid
        summaryTable2csvMtid
        summaryTable2engMtid
        summaryTable2templateMtid
        summaryTableCsvMtid
        summaryTableEngMtid
        summaryTableMtid
        template2Eng
        template2Hun
        templateEng
        templateHun
        temporaryAccessToken
        temporaryAccessTokenValidBefore
        unhandledCitationCount
        unhandledCitingPubCount
        unhandledTickets
        userNotificationTimeMtid
        username
        validFromYear
        validToYear
        wosCitationCount
        authorNames {
          __typename
          approved
          approverMtid
          comment
          comment2
          created
          creator
          deleted
          deletedDate
          error
          familyName
          fullName
          givenName
          labelEng
          labelHun
          lastModified
          lastModifier
          lastModifierAdmin
          lastRefresh
          lastTemplateMake
          lastTouched
          locked
          lockerMtid
          mtid
          oldId
          oldTimestamp
          otype
          prevValid
          published
          refreshed
          status
          template2Eng
          template2Hun
          templateEng
          templateHun
          unhandledTickets
          validFromYear
          validToYear
        }
      }
    }
  }
}

""".trimIndent()


val graphqlQueryExampleMtmtWithEverythingParameters = """
            query exampleQuery(${"$"}param: bigint!){
              publication(mtid:${"$"}param) {
                __everything            
                authorships {
                  __everything(except: [publicationMtid])            
                  author {
                    __everything            
                    authorNames {
                      __everything(except: [authorMtid])
                    }
                  }
                }
              }
            }
            """.trimIndent()

val mixedTypesInQuery = """
query {
    publication(mtid: 3156695) {
        __everything
        authorships {
            __everything(except: [publicationMtid, authorMtid])
            author {
                __everything
                authorNames {
                    __everything(except: [authorMtid])
                }
            }
        }
    }

    users(
        where:{mtid: {_eq: 521}}
    ) {
        __everything
        adminRoles {
            __everything(except: [userMtid])
            institute {
                __everything
                affiliations {
                    __everything
                    worksFor {
                        __everything
                    }
                }
            }
        }
    }
}
""".trimIndent()

val tweetRecursiveQuery = """
    query {
        __everything(except: [Stats])
        Author {
            __include(file: "include-tweet-author2.graphql")
        }
    }
""".trimIndent()

val tweetGraphql = """
    type Tweet {
        id: ID!
        # The tweet text. No more than 140 characters!
        body: String
        # When the tweet was published
        date: Date
        # Who published the tweet
        Author: User
        # Who edited the tweet
        Editor: User        
        # Views, retweets, likes, etc
        Stats: Stat
        ReplyTo: [Tweet]
    }

    type User {
        id: ID!
        username: String
        first_name: String
        last_name: String
        full_name: String
        name: String @deprecated
        avatar_url: Url
        friends: [User]
        tweets: [Tweet]
    }

    type Stat {
        views: Int
        likes: Int
        retweets: Int
        responses: Int
    }

    type Notification {
        id: ID
        date: Date
        type: String
    }

    type Meta {
        count: Int
    }

    scalar Url
    scalar Date

    type Query {
        Tweet(id: ID!): Tweet
        Tweets(limit: Int, skip: Int, sort_field: String, sort_order: String): [Tweet]
        TweetsMeta: Meta
        User(id: ID!): User
        Notifications(limit: Int): [Notification]
        NotificationsMeta: Meta
    }

    type Mutation {
        createTweet (
            body: String
        ): Tweet
        deleteTweet(id: ID!): Tweet
        markTweetRead(id: ID!): Boolean
    }
""".trimIndent()

val tweetQuery = """
    # Comment
    query testTweetQuery(${"$"}sort_field: String) {
       Tweets(limit: 10, sort_field: ${"$"}sort_field) {
            id
            body
            date
            __typename
            __EVERYTHING
            Author {
                username
                __typename
                id
                first_name
                last_name
                full_name   
                friends {
                    __EVERYTHING
                }
            }
       }
    }
""".trimIndent()

val tweetQueryExpaned = """query testTweetQuery(${"$"}sort_field: String)  {
  Tweets(limit: 10, sort_field: ${"$"}sort_field) {
    id
    body
    date
    __typename
    Author {
      username
      __typename
      id
      first_name
      last_name
      full_name
      friends {
        id
        username
        first_name
        last_name
        full_name
        name
        avatar_url
      }
    }
  }
}
"""

val tweetQueryExpanedAlwaysIncludeTypeName = """query testTweetQuery(${"$"}sort_field: String)  {
  Tweets(limit: 10, sort_field: ${"$"}sort_field) {
    id
    body
    date
    __typename
    Author {
      username
      __typename
      id
      first_name
      last_name
      full_name
      friends {
        __typename
        id
        username
        first_name
        last_name
        full_name
        name
        avatar_url
      }
    }
  }
}
"""

val tweetQueryWithUuInclude = """
    # Comment
    query testTweetQuery(${"$"}sort_field: String) {
       Tweets(limit: 10, sort_field: ${"$"}sort_field) {
            id
            body
            date
            __typename
            __include(files: ["include-tweet-author.graphql"])
       }

    }
""".trimIndent()


val graphqlQueryExampleWithRatings = """
            query exampleQuery(${"$"}param: bigint!){
              publication(mtid:${"$"}param) {
                __everything            
                authorships {
                  __everything            
                  author {
                    __everything            
                    authorNames {
                      __everything            
                    }
                  }
                }
                ratings {
                    __everything(except: [publicationMtid, ratingsMtid])
                    rating {
                        __everything(except: [periodicalMtid, ratingTypeMtid])
                        ratingType {
                            __everything
                        }
                    }
                }
              }
            }
            """.trimIndent()


val tweetQueryWithUuIncludeRecursive = """
    # Comment
    query testTweetQuery(${"$"}sort_field: String) {
       Tweets(limit: 10, sort_field: ${"$"}sort_field) {
            __include(file: "include-tweet.graphql")
       }
    }
""".trimIndent()
