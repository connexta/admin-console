import { expect } from 'chai'

import { groupResponses } from './source-discovery'

describe('Response Grouping', () => {
  describe('All Success', () => {
    const responses = [
      {
        type: 'SUCCESS',
        sourceType: 'CSW',
        value: 'cswSourceConfig'
      },
      {
        type: 'SUCCESS',
        sourceType: 'WFS',
        value: 'wfsSourceConfig'
      },
      {
        type: 'SUCCESS',
        sourceType: 'OpenSearch',
        value: 'openSearchSourceConfig'
      }
    ]

    it('Should return all source configurations', () => {
      const { foundSources } = groupResponses(responses)
      const responsesLength = responses.length
      const sourcesLength = Object.keys(foundSources).length
      expect(sourcesLength, `Only ${responsesLength} out of ${sourcesLength} configs were returned`)
        .to.equal(responsesLength)

      responses.forEach(({ sourceType }) => {
        expect(Object.keys(foundSources), `foundSources did not contain config for ${sourceType}`)
          .to.include(sourceType)
      })
    })
    it('Should return no uniqueErrors', () => {
      const { uniqueErrors } = groupResponses(responses)
      expect(uniqueErrors.length, 'Errors were returned').to.equal(0)
    })
    it('Should return no fatalErrors', () => {
      const { fatalErrors } = groupResponses(responses)
      expect(fatalErrors.length, 'Fatal Errors were returned').to.equal(0)
    })
  })

  describe('Mixed Errors (2 identical and 1 fatal)', () => {
    const responses = [
      {
        type: 'ERROR',
        sourceType: 'CSW',
        value: [{ message: 'UNKNOWN_ENDPOINT' }]
      },
      {
        type: 'ERROR',
        sourceType: 'WFS',
        value: [{ message: 'UNKNOWN_ENDPOINT' }]
      },
      {
        type: 'ERROR',
        sourceType: 'OpenSearch',
        value: [{ message: 'MISSING_REQUIRED_FIELD' }]
      }
    ]

    // const { foundSources, uniqueErrors, fatalErrors } = groupResponses(responses)

    it('Should return no sources', () => {
      const { foundSources } = groupResponses(responses)
      expect(Object.keys(foundSources).length, 'foundSources was not empty').to.equal(0)
    })
    it('Should return 2 uniqueErrors', () => {
      const { uniqueErrors } = groupResponses(responses)
      expect(uniqueErrors.length, `Returned ${uniqueErrors.length} errors instead of 2`).to.equal(2)
      expect(uniqueErrors, 'UNKNOWN_ENDPOINT error is missing').to.include('UNKNOWN_ENDPOINT')
      expect(uniqueErrors, 'MISSING_REQUIRED_FIELD error is missing').to.include('MISSING_REQUIRED_FIELD')
    })
    it('Should return 1 fatalError', () => {
      const { fatalErrors } = groupResponses(responses)
      expect(fatalErrors.length, `Returned ${fatalErrors.length} errors instead of 1`).to.equal(1)
      expect(fatalErrors, 'MISSING_REQUIRED_FIELD error is missing').to.include('MISSING_REQUIRED_FIELD')
    })
  })
})
