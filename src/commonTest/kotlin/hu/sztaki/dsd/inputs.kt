package hu.sztaki.dsd


data class SnapshotTest(
    val description: String,
    val graphql: String,
    val jsonResult: String,
    val expected: String
) {
}

val tests = mutableListOf<SnapshotTest>(
    SnapshotTest(
        description = "test1",
        graphql = """
            query {
              publication(mtid:3156694) {
                dtype
                otype
                mtid
                title
                authorships {
                  dtype
                  otype
                  mtid
                  familyName
                  givenName
                  author {
                    dtype
                    otype
                    mtid        
                    authorNames {
                      otype
                      mtid           
                      familyName
                      givenName
                      fullName
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
                  "mtid": "3156694",
                  "title": "Comparison of quenched and annealed invariance principles for random conductance model",
                  "acceptanceYear": null,
                  "authorships": [
                    {
                      "mtid": "4445519",
                      "familyName": "Barlow",
                      "givenName": "M",
                      "author": null
                    },
                    {
                      "mtid": "4445520",
                      "familyName": "Burdzy",
                      "givenName": "K",
                      "author": null
                    },
                    {
                      "mtid": "4445521",
                      "familyName": "Timar",
                      "givenName": "A",
                      "author": {
                        "mtid": "10030578",
                        "familyName": "Timár",
                        "givenName": "Ádám",
                        "authorNames": [
                          {
                            "fullName": "Timár Ádám",
                            "familyName": "Timár",
                            "givenName": "Ádám"
                          },
                          {
                            "fullName": "Ádám Timár",
                            "familyName": "Ádám",
                            "givenName": "Timár"
                          },
                          {
                            "fullName": "Tímár Á",
                            "familyName": "Tímár",
                            "givenName": "Á"
                          },
                          {
                            "fullName": "Á Tímár",
                            "familyName": "Á",
                            "givenName": "Tímár"
                          },
                          {
                            "fullName": "Timar Adam",
                            "familyName": "Timar",
                            "givenName": "Adam"
                          },
                          {
                            "fullName": "Adam Timar",
                            "familyName": "Adam",
                            "givenName": "Timar"
                          }
                        ]
                      }
                    }
                  ]
                }
              }
            }
            """.trimIndent(),
        expected = "TODO"
    )
)
