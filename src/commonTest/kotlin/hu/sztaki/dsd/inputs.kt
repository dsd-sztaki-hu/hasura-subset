package hu.sztaki.dsd


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
