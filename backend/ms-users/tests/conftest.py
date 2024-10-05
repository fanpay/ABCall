import os

os.environ["ENV"] = "test"


def pytest_configure(config):
    return config
