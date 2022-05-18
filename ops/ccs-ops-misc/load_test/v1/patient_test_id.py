'''Single Locust test for BFD endpoint'''

from common.bene_tests import BeneTestUser
from common.validation import SLA_PATIENT
from locust import task

class BFDUser(BeneTestUser):
    '''Single Locust test for BFD endpoint'''

    # The goals against which to measure these results. Note that they also include the Failsafe
    # cutoff, which will default to the V2 cutoff time if not set.
    VALIDATION_GOALS = SLA_PATIENT


    @task
    def patient_test_id(self):
        '''Patient search by ID'''
        self._test_v1_patient_test_id()
